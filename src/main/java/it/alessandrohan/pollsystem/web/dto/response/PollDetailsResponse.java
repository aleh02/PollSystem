package it.alessandrohan.pollsystem.web.dto.response;

import it.alessandrohan.pollsystem.model.PollStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class PollDetailsResponse {
    private Long id;
    private String owner;
    private Instant expiresAt;
    private PollStatus status;
    private WinnerOptionResponse winner; //null if not expired
    private List<PollOptionResponse> options;
}
