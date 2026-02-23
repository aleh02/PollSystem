package it.alessandrohan.pollsystem.service;

import it.alessandrohan.pollsystem.security.AuthPrincipal;
import it.alessandrohan.pollsystem.web.dto.request.PollOptionCreateRequest;
import it.alessandrohan.pollsystem.web.dto.request.PollOptionUpdateRequest;
import it.alessandrohan.pollsystem.web.dto.response.PollOptionResponse;

public interface PollOptionService {
      PollOptionResponse createPollOption(Long pollId, AuthPrincipal me, PollOptionCreateRequest request);

      PollOptionResponse getPollOption(Long pollId, Long optionId);

      PollOptionResponse updatePollOption(Long pollId, Long optionId, AuthPrincipal me, PollOptionUpdateRequest request);

      void deletePollOption(Long pollId, Long optionId, AuthPrincipal me);
}
