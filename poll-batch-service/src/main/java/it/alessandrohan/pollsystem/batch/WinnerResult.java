package it.alessandrohan.pollsystem.batch;

import java.math.BigDecimal;

public record WinnerResult(
        Long winnerOptionId,
        String winnerOptionMessage,
        BigDecimal winnerPercent
) {
}
