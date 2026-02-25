package it.alessandrohan.pollsystem.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PollBatchScheduler {
    private static final Logger log = LoggerFactory.getLogger(PollBatchScheduler.class);

    private final int batchLimit;
    private final PollExpirationService pollExpirationService;
    private final PollNotificationService pollNotificationService;

    public PollBatchScheduler(
            @Value("${app.batch.limit}") int batchLimit,
            PollExpirationService pollExpirationService,
            PollNotificationService pollNotificationService
    ) {
        this.batchLimit = batchLimit;
        this.pollExpirationService = pollExpirationService;
        this.pollNotificationService = pollNotificationService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Rome")
    public void runNightly() {
        runNow();
    }

    public void runNow() {
        long startedAt = System.currentTimeMillis();
        log.info("Batch run started (limit={})", batchLimit);

        int expired = pollExpirationService.expireDuePolls(batchLimit);
        int notified = pollNotificationService.notifyExpiredPolls(batchLimit);

        long elapsedMs = System.currentTimeMillis() - startedAt;
        log.info("Batch run completed: expired={}, notified={}, elapsedMs={}", expired, notified, elapsedMs);
    }
}
