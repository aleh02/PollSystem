package it.alessandrohan.pollsystem.batch;

import it.alessandrohan.pollsystem.model.Poll;
import it.alessandrohan.pollsystem.model.PollOption;
import it.alessandrohan.pollsystem.repository.PollOptionRepository;
import it.alessandrohan.pollsystem.repository.PollRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class PollExpirationService {
    private static final Logger log = LoggerFactory.getLogger(PollExpirationService.class);

    private final PollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final WinnerCalculator winnerCalculator;
    private final TransactionTemplate transactionTemplate;

    public PollExpirationService(
            PollRepository pollRepository,
            PollOptionRepository optionRepository,
            WinnerCalculator winnerCalculator,
            TransactionTemplate transactionTemplate
    ) {
        this.pollRepository = pollRepository;
        this.optionRepository = optionRepository;
        this.winnerCalculator = winnerCalculator;
        this.transactionTemplate = transactionTemplate;
    }

    public int expireDuePolls(int limit) {
        int effectiveLimit = Math.max(1, limit);
        int expiredCount = 0;

        while (true) {
            List<Long> duePollIds = pollRepository.findDuePollIds(effectiveLimit);
            if (duePollIds.isEmpty()) return expiredCount;

            for (Long pollId : duePollIds) {
                final int[] updated = new int[1];
                transactionTemplate.executeWithoutResult(status -> updated[0] = expireAndComputeWinner(pollId));
                expiredCount += updated[0];
            }

            if (duePollIds.size() < effectiveLimit) {
                log.info("Expire phase completed: {} polls expired", expiredCount);
                return expiredCount; //last page
            }
        }
    }

    private int expireAndComputeWinner(Long pollId) {
        int updated = pollRepository.expireIfActiveAndDue(pollId);
        if (updated == 0) return 0;

        WinnerResult result = winnerCalculator.calculate(pollId);

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("poll not found: " + pollId));

        if (result.winnerOptionId() == null) {
            poll.setWinnerOption(null);
            poll.setWinnerPercent(null);
        } else {
            PollOption winnerOption = optionRepository.findById(result.winnerOptionId())
                    .orElseThrow(() -> new IllegalArgumentException("winner option not found: " + result.winnerOptionId()));
            poll.setWinnerOption(winnerOption);
            poll.setWinnerPercent(result.winnerPercent());
        }

        pollRepository.save(poll);
        return 1;
    }
}
