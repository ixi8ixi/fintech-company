package com.academy.fintech.scoring.dto.scoring;

import java.math.BigDecimal;

public record ScoringDto(
        BigDecimal salary,
        BigDecimal requestedDisbursement,
        String clientId
) {}
