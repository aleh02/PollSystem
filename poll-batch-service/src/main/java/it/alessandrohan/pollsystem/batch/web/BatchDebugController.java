package it.alessandrohan.pollsystem.batch.web;

import it.alessandrohan.pollsystem.batch.scheduler.PollBatchScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@RestController
@RequestMapping("/internal/batch")
public class BatchDebugController {

    private final PollBatchScheduler pollBatchScheduler;

    public BatchDebugController(PollBatchScheduler pollBatchScheduler) {
        this.pollBatchScheduler = pollBatchScheduler;
    }

    @PostMapping("/run-now")
    public ResponseEntity<Void> runNow() {
        pollBatchScheduler.runNow();
        return ResponseEntity.accepted().build();
    }
}
