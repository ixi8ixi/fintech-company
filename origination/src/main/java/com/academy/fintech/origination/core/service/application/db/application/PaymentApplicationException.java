package com.academy.fintech.origination.core.service.application.db.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentApplicationException extends RuntimeException {
    private final String applicationId;
}
