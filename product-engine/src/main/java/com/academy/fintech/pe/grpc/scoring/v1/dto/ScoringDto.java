package com.academy.fintech.pe.grpc.scoring.v1.dto;

import java.math.BigDecimal;


public record ScoringDto(
   String clientId,
   BigDecimal requestedDisbursementAmount,
   BigDecimal interestRate,
   int loanTerm
) {}
