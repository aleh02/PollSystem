CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(60)  NOT NULL,
    email         VARCHAR(120) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE polls
(
    id               BIGSERIAL PRIMARY KEY,
    question         VARCHAR(500) NOT NULL,
    owner_id         BIGINT       NOT NULL REFERENCES users (id),
    expires_at       TIMESTAMPTZ  NOT NULL,
    status           VARCHAR(20)  NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    winner_option_id BIGINT NULL,
    winner_percent   NUMERIC(5, 2) NULL
);

CREATE INDEX idx_polls_status_expires_at ON polls (status, expires_at);

CREATE TABLE poll_options
(
    id         BIGSERIAL PRIMARY KEY,
    poll_id    BIGINT       NOT NULL REFERENCES polls (id),
    message    VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_poll_options_poll_id ON poll_options (poll_id);

ALTER TABLE polls
    ADD CONSTRAINT fk_polls_winner_option
        FOREIGN KEY (winner_option_id) REFERENCES poll_options (id);

CREATE TABLE votes
(
    id         BIGSERIAL PRIMARY KEY,
    poll_id    BIGINT      NOT NULL REFERENCES polls (id),
    user_id    BIGINT      NOT NULL REFERENCES users (id),
    option_id  BIGINT      NOT NULL REFERENCES poll_options (id),
    voted_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_votes_poll_user UNIQUE (poll_id, user_id)
);

CREATE INDEX idx_votes_poll_option ON votes (poll_id, option_id);
