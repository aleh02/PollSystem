package it.alessandrohan.pollsystem.service;

import it.alessandrohan.pollsystem.security.AuthPrincipal;
import it.alessandrohan.pollsystem.web.dto.response.PollOptionResponse;
import it.alessandrohan.pollsystem.web.dto.response.VoteResponse;

public interface VoteService {
    VoteResponse getVote(Long pollId, AuthPrincipal me);

    PollOptionResponse upsertVote(Long pollId, Long optionId, AuthPrincipal me);
}
