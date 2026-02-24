package it.alessandrohan.pollsystem.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;

    private Instant expiresAt;
}
