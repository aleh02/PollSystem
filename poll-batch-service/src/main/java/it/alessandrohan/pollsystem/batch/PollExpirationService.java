package it.alessandrohan.pollsystem.batch;

import it.alessandrohan.pollsystem.model.Poll;
import it.alessandrohan.pollsystem.model.PollOption;
import it.alessandrohan.pollsystem.repository.PollOptionRepository;
import it.alessandrohan.pollsystem.repository.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PollExpirationService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollOptionRepository optionRepository;

    @Autowired
    private WinnerCalculator winnerCalculator;

    public void expireDuePolls(int limit) {
        int effectiveLimit = Math.max(1, limit);

        while (true) {
            List<Long> duePollIds = pollRepository.findDuePollIds(effectiveLimit);
            if (duePollIds.isEmpty()) return;

            for (Long pollId : duePollIds) {
                expireAndComputeWinner(pollId);
            }

            if (duePollIds.size() < effectiveLimit) return; //last page
        }
    }

    @Transactional
    public void expireAndComputeWinner(Long pollId) {
        int updated = pollRepository.expireIfActiveAndDue(pollId);
        if (updated == 0) return;

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
    }
}
