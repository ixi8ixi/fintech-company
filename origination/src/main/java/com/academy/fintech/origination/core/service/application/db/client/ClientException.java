package com.academy.fintech.origination.core.service.application.db.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClientException extends RuntimeException {
    private final String clientEmail;
}
