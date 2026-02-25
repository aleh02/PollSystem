package it.alessandrohan.pollsystem.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PollBatchScheduler {

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Rome")
    public void runNightly() {
        // TODO implement batch
    }
}
