package it.alessandrohan.pollsystem.web.controller.v1;

import it.alessandrohan.pollsystem.service.PollService;
import it.alessandrohan.pollsystem.security.AuthPrincipal;
import it.alessandrohan.pollsystem.web.dto.request.PollCreateRequest;
import it.alessandrohan.pollsystem.web.dto.request.PollUpdateRequest;
import it.alessandrohan.pollsystem.web.dto.response.PollDetailsResponse;
import it.alessandrohan.pollsystem.web.dto.response.PollListPageResponse;
import it.alessandrohan.pollsystem.web.dto.response.PollResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PollController.BASE_URL)
public class PollController {
    public static final String BASE_URL = "/rest/api/v0";

    @Autowired
    private PollService pollService;

    @GetMapping("/polls")
    @ResponseStatus(HttpStatus.OK)
    public PollListPageResponse getPolls(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String search
    ) {
        return pollService.getPolls(page, size, search);
    }

    @GetMapping("/polls/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PollResponse getPollById(@PathVariable Long id) {
        return pollService.getPollById(id);
    }

    @PostMapping("/polls")
    @ResponseStatus(HttpStatus.CREATED)
    public PollResponse createPoll(@AuthenticationPrincipal AuthPrincipal me,
                                   @Valid @RequestBody PollCreateRequest request) {
        return pollService.createPoll(me, request);
    }

    @PutMapping("/polls/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public PollResponse updatePoll(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthPrincipal me,
            @Valid @RequestBody PollUpdateRequest request
    ) {
        return pollService.updatePoll(id, me, request);
    }

    @DeleteMapping("/polls/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void deletePoll(@PathVariable Long id,
                           @AuthenticationPrincipal AuthPrincipal me
    ) {
        pollService.deletePoll(id, me);
    }

    @GetMapping("/polls-details/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public PollDetailsResponse getPollDetails(@PathVariable Long id) {
        return pollService.getPollDetails(id);
    }
}
