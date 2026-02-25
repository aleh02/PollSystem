package it.alessandrohan.pollsystem.service;

import it.alessandrohan.pollsystem.mapper.PollOptionMapper;
import it.alessandrohan.pollsystem.model.Poll;
import it.alessandrohan.pollsystem.model.PollOption;
import it.alessandrohan.pollsystem.model.PollStatus;
import it.alessandrohan.pollsystem.repository.PollOptionRepository;
import it.alessandrohan.pollsystem.repository.PollRepository;
import it.alessandrohan.pollsystem.repository.VoteRepository;
import it.alessandrohan.pollsystem.security.AuthPrincipal;
import it.alessandrohan.pollsystem.web.dto.request.PollOptionCreateRequest;
import it.alessandrohan.pollsystem.web.dto.request.PollOptionUpdateRequest;
import it.alessandrohan.pollsystem.web.dto.response.PollOptionResponse;
import it.alessandrohan.pollsystem.web.exception.BadRequestException;
import it.alessandrohan.pollsystem.web.exception.UnauthorizedOperationException;
import it.alessandrohan.pollsystem.web.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PollOptionServiceImpl implements PollOptionService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final VoteRepository voteRepository;
    private final PollOptionMapper pollOptionMapper;

    public PollOptionServiceImpl(
            PollRepository pollRepository,
            PollOptionRepository pollOptionRepository,
            VoteRepository voteRepository,
            PollOptionMapper pollOptionMapper
    ) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.voteRepository = voteRepository;
        this.pollOptionMapper = pollOptionMapper;
    }

    @Override
    public PollOptionResponse createPollOption(Long pollId, AuthPrincipal me, PollOptionCreateRequest request) {
        Poll poll = pollRepository.findWithOwnerById(pollId)
                .orElseThrow(() -> new NotFoundException("poll not found"));

        if (!poll.getOwner().getId().equals(me.userId())) throw new UnauthorizedOperationException("unauthorized");

        Instant now = Instant.now();
        if (poll.getStatus() == PollStatus.EXPIRED || !poll.getExpiresAt().isAfter(now))
            throw new BadRequestException("poll expired");

        PollOption option = pollOptionMapper.pollOptionCreateReqToPollOption(request);
        option.setPoll(poll);
        pollOptionRepository.save(option);

        return pollOptionMapper.pollOptionToPollOptionResponse(option);
    }

    @Override
    public PollOptionResponse getPollOption(Long pollId, Long optionId) {
        PollOption option = pollOptionRepository.findById(optionId)
                .orElseThrow(() -> new NotFoundException("option not found"));

        if (!option.getPoll().getId().equals(pollId))
            throw new NotFoundException("option not found in this poll");

        return pollOptionMapper.pollOptionToPollOptionResponse(option);
    }

    @Override
    public PollOptionResponse updatePollOption(
            Long pollId, Long optionId, AuthPrincipal me, PollOptionUpdateRequest request
    ) {
        PollOption option = pollOptionRepository.findById(optionId)
                .orElseThrow(() -> new NotFoundException("option not found"));

        if (!option.getPoll().getId().equals(pollId))
            throw new NotFoundException("option not found in this poll");

        if (!option.getPoll().getOwner().getId().equals(me.userId()))
            throw new UnauthorizedOperationException("unauthorized");

        if (voteRepository.existsByPollOptionId(optionId))
            throw new BadRequestException("unable to modify voted options");

        option.setMessage(request.getMessage());
        pollOptionRepository.save(option);

        return pollOptionMapper.pollOptionToPollOptionResponse(option);
    }

    @Override
    public void deletePollOption(Long pollId, Long optionId, AuthPrincipal me) {
        PollOption option = pollOptionRepository.findById(optionId)
                .orElseThrow(() -> new NotFoundException("option not found"));

        if (!option.getPoll().getId().equals(pollId))
            throw new NotFoundException("option not found in this poll");

        if (!option.getPoll().getOwner().getId().equals(me.userId()))
            throw new UnauthorizedOperationException("unauthorized");

        if (voteRepository.existsByPollOptionId(optionId))
            throw new BadRequestException("unable to delete voted options");

        pollOptionRepository.deleteById(optionId);
    }
}
