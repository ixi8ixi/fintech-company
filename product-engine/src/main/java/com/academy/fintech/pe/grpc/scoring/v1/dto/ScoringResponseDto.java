package com.academy.fintech.pe.grpc.scoring.v1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ScoringResponseDto(
    BigDecimal pmt,
    List<LocalDate> paymentDates
) {}
