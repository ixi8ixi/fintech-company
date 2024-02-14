package com.academy.fintech.pe.core.service.agreement.operation.result;

import lombok.Getter;

/**
 * A wrapper class for returning results from the service, with an explanatory message if necessary.
 *
 * @param <T> The value type returned from the service
 */
@Getter
public class OperationResult<T> {
    private static final String DEFAULT_MESSAGE = "";
    private final T value;
    private final String message;

    private OperationResult(T value, String message) {
        this.value = value;
        this.message = message;
    }

    public static <T> OperationResult<T> of(T value, String message) {
        return new OperationResult<>(value, message);
    }

    public static <T> OperationResult<T> of(T value) {
        return new OperationResult<>(value, DEFAULT_MESSAGE);
    }
}
