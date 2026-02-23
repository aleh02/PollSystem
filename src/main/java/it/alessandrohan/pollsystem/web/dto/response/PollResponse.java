package it.alessandrohan.pollsystem.web.dto.response;

import it.alessandrohan.pollsystem.model.PollStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class PollResponse {

    private Long id;

    private String question;

    private String owner; //username

    private Instant expiresAt;

    private PollStatus status;
}
