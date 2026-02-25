package it.alessandrohan.pollsystem.batch;

import it.alessandrohan.pollsystem.messaging.WinnerMessage;
import it.alessandrohan.pollsystem.model.Poll;
import it.alessandrohan.pollsystem.repository.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class PollNotificationService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private WinnerMailProducer winnerMailProducer;

    public void notifyExpiredPolls(int limit) {
        int effectiveLimit = Math.max(1, limit);

        while (true) {
            List<Long> pollIds = pollRepository.findExpiredNotNotifiedPollIds(effectiveLimit);
            if(pollIds.isEmpty()) return;

            for(Long pollId : pollIds) {
                notifyOne(pollId);
            }

            if(pollIds.size() < effectiveLimit) return;
        }
    }

    @Transactional
    protected void markNotified(Long pollId) {
        pollRepository.markWinnerNotified(pollId);
    }

    private void notifyOne(Long pollId) {
        Poll poll = pollRepository.findWithOwnerAndWinnerOptionById(pollId)
                .orElse(null);
        if(poll == null || poll.getWinnerNotifiedAt() != null) return;

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
    }
}
