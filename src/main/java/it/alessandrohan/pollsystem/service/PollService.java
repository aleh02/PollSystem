package it.alessandrohan.pollsystem.service;

import it.alessandrohan.pollsystem.web.dto.request.PollCreateRequest;
import it.alessandrohan.pollsystem.web.dto.request.PollUpdateRequest;
import it.alessandrohan.pollsystem.web.dto.response.PollListPageResponse;
import it.alessandrohan.pollsystem.web.dto.response.PollResponse;
import it.alessandrohan.pollsystem.security.AuthPrincipal;

public interface PollService {
    PollListPageResponse getPolls(int page, int size, String search);

    PollResponse createPoll(AuthPrincipal me, PollCreateRequest request);

    PollResponse getPollById(Long id);

    PollResponse updatePoll(Long id, AuthPrincipal me, PollUpdateRequest request);

    void deletePoll(Long id, AuthPrincipal me);
}
