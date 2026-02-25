package it.alessandrohan.pollsystem.batch;

import it.alessandrohan.pollsystem.repository.PollOptionRepository;
import it.alessandrohan.pollsystem.repository.VoteRepository;
import it.alessandrohan.pollsystem.repository.WinnerOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class WinnerCalculator {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollOptionRepository optionRepository;

    public WinnerResult calculate(Long pollId) {
        long totalVotes = voteRepository.countByPollId(pollId);
        if(totalVotes == 0)
            return new WinnerResult(null, null, null);

        WinnerOption winner = optionRepository.findWinnerForPoll(pollId)
                .orElseThrow(() -> new IllegalStateException("top option not found for poll " + pollId));

        BigDecimal percent = BigDecimal.valueOf(winner.getVotesCount())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalVotes), 2, RoundingMode.HALF_UP);   //2 decimals

        return new WinnerResult(winner.getOptionId(), winner.getOptionMessage(), percent);
    }
}
