package it.alessandrohan.pollsystem.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WinnerOptionResponse {
    private Long pollId;
    private Long optionId;
    private BigDecimal percentOfWiner;
}
