package it.alessandrohan.pollsystem.batch.expiration;

import it.alessandrohan.pollsystem.repository.PollOptionRepository;
import it.alessandrohan.pollsystem.repository.VoteRepository;
import it.alessandrohan.pollsystem.repository.WinnerOption;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class WinnerCalculator {

    private final VoteRepository voteRepository;
    private final PollOptionRepository optionRepository;

    public WinnerCalculator(VoteRepository voteRepository, PollOptionRepository optionRepository) {
        this.voteRepository = voteRepository;
        this.optionRepository = optionRepository;
    }

    public WinnerResult calculate(Long pollId) {
        long totalVotes = voteRepository.countByPollId(pollId);
        if(totalVotes == 0)
            return new WinnerResult(null, null, null);

        WinnerOption winner = optionRepository.findWinnerForPoll(pollId)
                .orElseThrow(() -> new IllegalStateException("top option not found for poll " + pollId));

        Long voteCount = winner.getVoteCount();
        if (voteCount == null) {
            throw new IllegalStateException("winner voteCount is null for poll " + pollId);
        }

        BigDecimal percent = BigDecimal.valueOf(voteCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalVotes), 2, RoundingMode.HALF_UP);   //2 decimals

        return new WinnerResult(winner.getOptionId(), winner.getOptionMessage(), percent);
    }
}
