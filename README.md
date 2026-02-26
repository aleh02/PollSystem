# Poll System

Spring multi-module platform for creating, voting, and managing polls with scheduled winner notification workflows.

## Overview

The platform allows users to:
- register and login with JWT
- create polls with expiration date (`expiresAt`)
- manage poll options
- vote one option per poll (vote replacement supported)
- receive winner notification at poll expiration through RabbitMQ message

Batch behavior:
- expire due polls
- compute winner and winner percentage
- publish winner payload to queue `poll.winner.mail`
- mark poll as notified (idempotent)

## Project structure

- `poll-domain`: shared domain model, repositories
- `poll-rest-service`: REST APIs + JWT auth + Flyway migrations
- `poll-batch-service`: scheduler + winner computation + Rabbit producer
- `postman/pollsystem_full_tests.postman_collection.json`: API test collection

## Tech stack

- Java 21
- Spring Boot 4.0.2
- Spring Web / Security / Data JPA
- PostgreSQL
- Flyway
- RabbitMQ
- Maven wrapper (`./mvnw`)

## Local prerequisites

- JDK 21
- Docker + Docker Compose
- Maven wrapper executable (`./mvnw`)

## Infrastructure startup

From project root:

```bash
docker compose up -d
```

Services:
- PostgreSQL: `localhost:5434` (`poll_system` / `poll_user` / `poll_pass`)
- RabbitMQ broker: `localhost:5672`
- RabbitMQ UI: `http://localhost:15672` (`guest` / `guest`)

## Build

```bash
./mvnw clean install -DskipTests
```

## Run services

### REST service

```bash
./mvnw -f poll-rest-service/pom.xml spring-boot:run
```

- Base URL: `http://localhost:8080/rest/api/v0`
- Flyway is enabled here and applies migrations at startup.

### Batch service

```bash
./mvnw -f poll-batch-service/pom.xml spring-boot:run
```

- Port: `8081`
- Nightly schedule: `@Scheduled(cron = "0 0 0 * * *", zone = "Europe/Rome")`
- Batch logs include expired/notified counters and elapsed time.

### Batch manual trigger (local profile only)

The debug endpoint is available only with `local` profile.

Run batch with local profile:

```bash
./mvnw -f poll-batch-service/pom.xml spring-boot:run -Dspring-boot.run.profiles=local
```

Trigger immediately:

```bash
curl -X POST http://localhost:8081/internal/batch/run-now
```

PowerShell alternative:

```powershell
Invoke-RestMethod -Method POST -Uri "http://localhost:8081/internal/batch/run-now"
```

## API quick flow

1. `POST /registration`
2. `POST /login`
3. `POST /polls`
4. `POST /polls/{id}/options`
5. `PUT /polls/{id}/options/{optionId}/vote`

Auth header:

```text
Authorization: Bearer <jwt>
```

## Postman tests

Import:
- `postman/pollsystem_full_tests.postman_collection.json`

Collection includes:
- auth tests
- poll CRUD tests
- options and vote tests
- post-vote negative tests (update/delete voted option must fail)

## Time and expiration behavior

`expiresAt` is normalized in business timezone (`Europe/Rome`) to next midnight boundary.

Practical effect:
- creating a poll with near-future instant still expires at midnight logic
- for fast local batch testing, force DB expiration manually

Example:

```sql
UPDATE polls
SET expires_at = now() - interval '1 minute'
WHERE id = <poll_id>;
```

## Batch verification checklist

After batch run, verify in DB:

```sql
SELECT id, status, winner_option_id, winner_percent, winner_notified_at, expires_at
FROM polls
ORDER BY id DESC
LIMIT 20;
```

Expected:
- `status = EXPIRED`
- winner fields filled (or null when no votes)
- `winner_notified_at` set after successful publish

RabbitMQ verification:
- queue: `poll.winner.mail`
- message payload fields:
  - `pollQuestion`
  - `winnerOption`
  - `winnerPercent`
  - `expiredAt`
  - `ownerEmail`

## Flyway migrations

Migrations live in:
- `poll-rest-service/src/main/resources/db/migration`

Current scripts:
- `V1__init.sql`
- `V2__add_winner_notified_at.sql`

If schema is out-of-sync, start REST service and check Flyway logs.

## Common issues

### `spring-boot:run` fails on parent with main class error

Cause: running plugin on aggregator POM.

Use module command instead:

```bash
./mvnw -f poll-rest-service/pom.xml spring-boot:run
./mvnw -f poll-batch-service/pom.xml spring-boot:run
```

### `Could not find artifact ... poll-domain`

Install modules first:

```bash
./mvnw clean install -DskipTests
```

### Batch debug endpoint returns 404/401

- ensure batch runs on `8081`
- ensure profile is `local` (endpoint is profile-scoped)

### `NoSuchMethodError` after interface changes

Rebuild all modules and restart app:

```bash
./mvnw clean install -DskipTests
```

## Notes

- DB timestamps are stored in UTC (`hibernate.jdbc.time_zone=UTC`).
- Batch and business date conversion use `Europe/Rome` where required.
- Queue publishing is decoupled from email sending (consumer is external).
