package it.alessandrohan.pollsystem.messaging;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WinnerMessage(
        String pollQuestion,
        String winnerOption,
        BigDecimal winnerPercent,
        LocalDate expiredAt,
        String ownerEmail
) {
}
