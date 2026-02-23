package it.alessandrohan.pollsystem.service;

import it.alessandrohan.pollsystem.mapper.PollMapper;
import it.alessandrohan.pollsystem.model.Poll;
import it.alessandrohan.pollsystem.model.PollStatus;
import it.alessandrohan.pollsystem.model.User;
import it.alessandrohan.pollsystem.repository.PollRepository;
import it.alessandrohan.pollsystem.repository.UserRepository;
import it.alessandrohan.pollsystem.repository.VoteRepository;
import it.alessandrohan.pollsystem.security.AuthPrincipal;
import it.alessandrohan.pollsystem.web.dto.request.PollCreateRequest;
import it.alessandrohan.pollsystem.web.dto.request.PollUpdateRequest;
import it.alessandrohan.pollsystem.web.exception.BadRequestException;
import it.alessandrohan.pollsystem.web.exception.UnauthorizedOperationException;
import it.alessandrohan.pollsystem.web.exception.NotFoundException;
import it.alessandrohan.pollsystem.web.dto.response.PollListPageResponse;
import it.alessandrohan.pollsystem.web.dto.response.PollResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Service
public class PollServiceImpl implements PollService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollMapper pollMapper;

    @Override
    public PollListPageResponse getPolls(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Poll> pollPage = StringUtils.hasText(search)
                ? pollRepository.findByQuestionContainingIgnoreCase(search, pageable)
                : pollRepository.findAll(pageable);

        return new PollListPageResponse(
                pollPage.isFirst(),
                pollPage.isLast(),
                pollPage.getSize(),
                pollPage.getTotalElements(),
                pollPage.getTotalPages(),
                pollPage.getNumber(),
                pollMapper.pollListToPollResponseList(pollPage.getContent())
        );
    }

    @Override
    public PollResponse createPoll(AuthPrincipal me, PollCreateRequest request) {
        Instant now = Instant.now();
        if (request.getExpiresAt().isBefore(now)) { //400
            throw new IllegalArgumentException("expiresAt must be in the future");
        }

        User owner = userRepository.findById(me.userId())
                .orElseThrow(() -> new NotFoundException("user not found"));

        Poll poll = pollMapper.pollCreateReqToPoll(request);
        poll.setOwner(owner);
        poll.setStatus(PollStatus.ACTIVE);
        poll.setWinnerOption(null);
        poll.setWinnerPercent(null);

        Poll savedPoll = pollRepository.save(poll);
        return pollMapper.pollToPollResponse(savedPoll);
    }

    @Override
    public PollResponse getPollById(Long id) {
        return pollMapper.pollToPollResponse(
                pollRepository.findWithOwnerById(id)
                        .orElseThrow(() -> new NotFoundException("poll not found"))
        );
    }

    @Override
    public PollResponse updatePoll(Long id, AuthPrincipal me, PollUpdateRequest request) {
        if (request.getQuestion() == null && request.getExpiresAt() == null) {
            throw new IllegalArgumentException("question or expiration time not valid");
        }

        Poll poll = pollRepository.findWithOwnerById(id)
                .orElseThrow(() -> new NotFoundException("poll not found"));

        if(!poll.getOwner().getId().equals(me.userId()))
            throw new UnauthorizedOperationException("unauthorized");

        Instant now = Instant.now();
        if (poll.getStatus() == PollStatus.EXPIRED || !poll.getExpiresAt().isAfter(now)) {
            throw new BadRequestException("poll expired");
        }

        if(voteRepository.existsByPollId(id)) throw new BadRequestException("unable to modify voted polls");

        if (request.getQuestion() != null) poll.setQuestion(request.getQuestion());
        if (request.getExpiresAt() != null) {
            if (!request.getExpiresAt().isAfter(now)) {
                throw new IllegalArgumentException("expiresAt must be in the future");
            }
            poll.setExpiresAt(request.getExpiresAt());
        }
        Poll savedPoll = pollRepository.save(poll);

        return pollMapper.pollToPollResponse(savedPoll);
    }

    @Override
    public void deletePoll(Long id, AuthPrincipal me) {
        Poll poll = pollRepository.findWithOwnerById(id)
                .orElseThrow(() -> new NotFoundException("poll not found"));

        if(!poll.getOwner().getId().equals(me.userId()))
            throw new UnauthorizedOperationException("unauthorized");

        pollRepository.delete(poll);
    }
}
