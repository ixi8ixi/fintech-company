package com.academy.fintech.origination.grpc.application.v1.dto;

import java.math.BigDecimal;

/**
 * Data transfer object for information to create a new application.
 */
public record ApplicationCreationDto(
        String firstName,
        String lastName,
        String email,
        BigDecimal salary,
        BigDecimal requestedDisbursementAmount
) {}
