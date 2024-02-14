package com.academy.fintech.origination.grpc.application.v1.dto;

import java.math.BigDecimal;

/**
 * Data transfer object for information on evaluating an application in the scoring service.
 */
public record ApplicationScoringDto(
        String applicationId,
        String clientId,
        BigDecimal salary,
        BigDecimal requestedDisbursementAmount
) {}
