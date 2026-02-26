package it.alessandrohan.pollsystem.batch.expiration;

import java.math.BigDecimal;

public record WinnerResult(
        Long winnerOptionId,
        String winnerOptionMessage,
        BigDecimal winnerPercent
) {
}
