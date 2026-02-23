package it.alessandrohan.pollsystem.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class PollOptionResponse {

    private Long id;
    private String message;
    private Instant createdAt;
}
