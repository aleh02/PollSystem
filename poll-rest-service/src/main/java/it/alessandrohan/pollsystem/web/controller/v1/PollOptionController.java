package it.alessandrohan.pollsystem.web.controller.v1;

import it.alessandrohan.pollsystem.security.AuthPrincipal;
import it.alessandrohan.pollsystem.service.PollOptionService;
import it.alessandrohan.pollsystem.web.dto.request.PollOptionCreateRequest;
import it.alessandrohan.pollsystem.web.dto.request.PollOptionUpdateRequest;
import it.alessandrohan.pollsystem.web.dto.response.PollOptionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PollOptionController.BASE_URL)
public class PollOptionController {
    public static final String BASE_URL = "/rest/api/v0/polls";

    private final PollOptionService pollOptionService;

    public PollOptionController(PollOptionService pollOptionService) {
        this.pollOptionService = pollOptionService;
    }

    @PostMapping("/{id}/options")
    @ResponseStatus(HttpStatus.CREATED)
    public PollOptionResponse createPollOption(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthPrincipal me,
            @Valid @RequestBody PollOptionCreateRequest request
    ) {
        return pollOptionService.createPollOption(id, me, request);
    }

    @GetMapping("/{id}/options/{optionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PollOptionResponse getPollOption(
            @PathVariable Long id,
            @PathVariable Long optionId
    ) {
        return pollOptionService.getPollOption(id, optionId);
    }

    @PutMapping("/{id}/options/{optionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PollOptionResponse updatePollOption(
            @PathVariable Long id,
            @PathVariable Long optionId,
            @AuthenticationPrincipal AuthPrincipal me,
            @Valid @RequestBody PollOptionUpdateRequest request
    ) {
        return pollOptionService.updatePollOption(id, optionId, me, request);
    }

    @DeleteMapping("/{id}/options/{optionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void deletePollOption(
            @PathVariable Long id,
            @PathVariable Long optionId,
            @AuthenticationPrincipal AuthPrincipal me
    ) {
        pollOptionService.deletePollOption(id, optionId, me);
    }
}
