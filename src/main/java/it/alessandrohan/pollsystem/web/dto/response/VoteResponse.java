package it.alessandrohan.pollsystem.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class VoteResponse {

    private Long optionId;

    private Long id;

    private Instant votedAt;
}
