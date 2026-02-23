package it.alessandrohan.pollsystem.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollUpdateRequest {

    @NotBlank
    private String question;

    @NotNull
    private Instant expiresAt;
}
