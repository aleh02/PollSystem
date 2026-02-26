package it.alessandrohan.pollsystem.batch.notification;

import it.alessandrohan.pollsystem.messaging.WinnerMessage;
import it.alessandrohan.pollsystem.model.Poll;
import it.alessandrohan.pollsystem.repository.PollRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class PollNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PollNotificationService.class);

    private final PollRepository pollRepository;
    private final WinnerMailProducer winnerMailProducer;
    private final TransactionTemplate transactionTemplate;

    public PollNotificationService(
            PollRepository pollRepository,
            WinnerMailProducer winnerMailProducer,
            TransactionTemplate transactionTemplate
    ) {
        this.pollRepository = pollRepository;
        this.winnerMailProducer = winnerMailProducer;
        this.transactionTemplate = transactionTemplate;
    }

    public int notifyExpiredPolls(int limit) {
        int effectiveLimit = Math.max(1, limit);
        int notifiedCount = 0;

        while (true) {
            List<Long> pollIds = pollRepository.findExpiredNotNotifiedPollIds(effectiveLimit);
            if (pollIds.isEmpty()) return notifiedCount;

            for (Long pollId : pollIds) {
                try {
                    notifiedCount += notifyOne(pollId);
                } catch (Exception ex) {
                    //keeps processing if one publish fails.
                    log.error("Failed to notify winner for poll {}", pollId, ex);
                }
            }

            if (pollIds.size() < effectiveLimit) return notifiedCount;
        }
    }

    protected void markNotified(Long pollId) {
        transactionTemplate.executeWithoutResult(status -> {
            int updated = pollRepository.markWinnerNotified(pollId);
            if (updated == 0) {
                log.debug("Poll {} was already marked as notified", pollId);
            }
        });
    }

    private int notifyOne(Long pollId) {
        Poll poll = pollRepository.findWithOwnerAndWinnerOptionById(pollId)
                .orElse(null);
        if (poll == null || poll.getWinnerNotifiedAt() != null) return 0;

        LocalDate expiredAt = poll.getExpiresAt()
                .atZone(ZoneId.of("Europe/Rome"))
                .minusDays(1)
                .toLocalDate();

        WinnerMessage msg = new WinnerMessage(
                poll.getQuestion(),
                poll.getWinnerOption() != null ? poll.getWinnerOption().getMessage() : null,
                poll.getWinnerPercent(),
                expiredAt,
                poll.getOwner().getEmail()
        );

        winnerMailProducer.send(msg);
        markNotified(pollId);   //only if publish success
        return 1;
    }
}
