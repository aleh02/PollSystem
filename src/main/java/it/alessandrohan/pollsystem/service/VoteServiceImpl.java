package it.alessandrohan.pollsystem.service;

import it.alessandrohan.pollsystem.mapper.PollOptionMapper;
import it.alessandrohan.pollsystem.mapper.VoteMapper;
import it.alessandrohan.pollsystem.model.Poll;
import it.alessandrohan.pollsystem.model.PollOption;
import it.alessandrohan.pollsystem.model.Vote;
import it.alessandrohan.pollsystem.repository.PollOptionRepository;
import it.alessandrohan.pollsystem.repository.PollRepository;
import it.alessandrohan.pollsystem.repository.VoteRepository;
import it.alessandrohan.pollsystem.security.AuthPrincipal;
import it.alessandrohan.pollsystem.web.dto.response.PollOptionResponse;
import it.alessandrohan.pollsystem.web.dto.response.VoteResponse;
import it.alessandrohan.pollsystem.web.exception.BadRequestException;
import it.alessandrohan.pollsystem.web.exception.NotFoundException;
import it.alessandrohan.pollsystem.web.exception.UnauthorizedOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollOptionRepository pollOptionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteMapper voteMapper;

    @Autowired
    private PollOptionMapper pollOptionMapper;

    @Override
    public VoteResponse getVote(Long pollId, AuthPrincipal me) {
        if(!pollRepository.existsById(pollId))
            throw new NotFoundException("poll not found");

        Vote vote = voteRepository.findByPollIdAndUserId(pollId, me.userId())
                .orElseThrow(() -> new BadRequestException("no vote in this poll"));

        return voteMapper.voteToVoteResponse(vote);
    }

    @Transactional
    @Override
    public PollOptionResponse upsertVote(Long pollId, Long optionId, AuthPrincipal me) {
        //CAS
        int cas = pollRepository.casVoteAllowed(pollId);

        if(cas == 0){
            if(!pollRepository.existsById(pollId))
                throw new NotFoundException("poll not found");
            throw new BadRequestException("poll expired");
        }

        Long ownerId = pollRepository.findOwnerId(pollId);
        if(ownerId != null && ownerId.equals(me.userId()))
            throw new UnauthorizedOperationException("owner cannot vote");

        if(!pollOptionRepository.existsByIdAndPollId(optionId, pollId))
            throw new NotFoundException("option not found in this poll");

        //UPSERT
        voteRepository.upsertVote(pollId, me.userId(), optionId);

        PollOption option = pollOptionRepository.findById(optionId)
                .orElseThrow(() -> new NotFoundException("option not found"));
        return pollOptionMapper.pollOptionToPollOptionResponse(option);
    }
}
