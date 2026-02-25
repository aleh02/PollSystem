package it.alessandrohan.pollsystem.web.controller.v1;

import it.alessandrohan.pollsystem.security.AuthPrincipal;
import it.alessandrohan.pollsystem.service.VoteService;
import it.alessandrohan.pollsystem.web.dto.response.PollOptionResponse;
import it.alessandrohan.pollsystem.web.dto.response.VoteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(VoteController.BASE_URL)
public class VoteController {
    public static final String BASE_URL = "/rest/api/v0/polls";

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("/{id}/vote")
    @ResponseStatus(HttpStatus.OK)
    public VoteResponse getVote(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthPrincipal me
    ) {
        return voteService.getVote(id, me);
    }

    @PutMapping("/{id}/options/{optionId}/vote")
    @ResponseStatus(HttpStatus.CREATED)
    public PollOptionResponse votePollOption(
            @PathVariable Long id,
            @PathVariable Long optionId,
            @AuthenticationPrincipal AuthPrincipal me
    ) {
        return voteService.upsertVote(id, optionId, me);
    }
}
