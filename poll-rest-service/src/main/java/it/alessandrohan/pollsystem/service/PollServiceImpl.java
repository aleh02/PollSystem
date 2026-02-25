package it.alessandrohan.pollsystem.service;

import it.alessandrohan.pollsystem.mapper.PollMapper;
import it.alessandrohan.pollsystem.mapper.PollOptionMapper;
import it.alessandrohan.pollsystem.model.Poll;
import it.alessandrohan.pollsystem.model.PollOption;
import it.alessandrohan.pollsystem.model.PollStatus;
import it.alessandrohan.pollsystem.model.User;
import it.alessandrohan.pollsystem.repository.PollOptionRepository;
import it.alessandrohan.pollsystem.repository.PollRepository;
import it.alessandrohan.pollsystem.repository.UserRepository;
import it.alessandrohan.pollsystem.repository.VoteRepository;
import it.alessandrohan.pollsystem.security.AuthPrincipal;
import it.alessandrohan.pollsystem.web.dto.request.PollCreateRequest;
import it.alessandrohan.pollsystem.web.dto.request.PollUpdateRequest;
import it.alessandrohan.pollsystem.web.dto.response.*;
import it.alessandrohan.pollsystem.web.exception.BadRequestException;
import it.alessandrohan.pollsystem.web.exception.UnauthorizedOperationException;
import it.alessandrohan.pollsystem.web.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class PollServiceImpl implements PollService {
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Europe/Rome");

    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final PollMapper pollMapper;
    private final PollOptionRepository pollOptionRepository;
    private final PollOptionMapper pollOptionMapper;

    public PollServiceImpl(
            PollRepository pollRepository,
            UserRepository userRepository,
            VoteRepository voteRepository,
            PollMapper pollMapper,
            PollOptionRepository pollOptionRepository,
            PollOptionMapper pollOptionMapper
    ) {
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.pollMapper = pollMapper;
        this.pollOptionRepository = pollOptionRepository;
        this.pollOptionMapper = pollOptionMapper;
    }

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
        Instant normalizedExpiresAt = normalizeToNextMidnightBusinessZone(request.getExpiresAt());
        if (!normalizedExpiresAt.isAfter(now)) {
            throw new IllegalArgumentException("expiresAt must be in the future");
        }

        User owner = userRepository.findById(me.userId())
                .orElseThrow(() -> new NotFoundException("user not found"));

        Poll poll = pollMapper.pollCreateReqToPoll(request);
        poll.setOwner(owner);
        poll.setExpiresAt(normalizedExpiresAt);
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

        if (!poll.getOwner().getId().equals(me.userId()))
            throw new UnauthorizedOperationException("unauthorized");

        Instant now = Instant.now();
        if (poll.getStatus() == PollStatus.EXPIRED || !poll.getExpiresAt().isAfter(now)) {
            throw new BadRequestException("poll expired");
        }

        if (voteRepository.existsByPollId(id)) throw new BadRequestException("unable to modify voted polls");

        if (request.getQuestion() != null) poll.setQuestion(request.getQuestion());
        if (request.getExpiresAt() != null) {
            Instant normalizedExpiresAt = normalizeToNextMidnightBusinessZone(request.getExpiresAt());
            if (!normalizedExpiresAt.isAfter(now)) {
                throw new IllegalArgumentException("expiresAt must be in the future");
            }
            poll.setExpiresAt(normalizedExpiresAt);
        }
        Poll savedPoll = pollRepository.save(poll);

        return pollMapper.pollToPollResponse(savedPoll);
    }

    @Override
    public void deletePoll(Long id, AuthPrincipal me) {
        Poll poll = pollRepository.findWithOwnerById(id)
                .orElseThrow(() -> new NotFoundException("poll not found"));

        if (!poll.getOwner().getId().equals(me.userId()))
            throw new UnauthorizedOperationException("unauthorized");

        pollRepository.delete(poll);
    }

    @Override
    public PollDetailsResponse getPollDetails(Long pollId) {
        Poll poll = pollRepository.findWithOwnerById(pollId)
                .orElseThrow(() -> new NotFoundException("poll not found"));

        PollDetailsResponse response = pollMapper.polltoPollDetailsResponse(poll);
        List<PollOption> options = pollOptionRepository.findAllByPollId(pollId);
        response.setOptions(pollOptionMapper.pollOptionListToPollOptionResponseList(options));

        if (poll.getStatus() == PollStatus.EXPIRED
                && poll.getWinnerOption() != null
                && poll.getWinnerPercent() != null
        ) {
            response.setWinner(new WinnerOptionResponse(
                    poll.getId(),
                    poll.getWinnerOption().getId(),
                    poll.getWinnerPercent()
            ));
        } else {
            response.setWinner(null);
        }

        return response;
    }

    private Instant normalizeToNextMidnightBusinessZone(Instant expiresAtInput) {
        LocalDate expiresDateInBusinessZone = expiresAtInput.atZone(BUSINESS_ZONE).toLocalDate();
        return expiresDateInBusinessZone
                .plusDays(1)
                .atStartOfDay(BUSINESS_ZONE)
                .toInstant();
    }
}
