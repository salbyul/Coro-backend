package com.coro.coro.common.exception;

import com.coro.coro.common.response.error.ErrorType;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final String domain;
    private final ErrorType errorType;

    public GlobalException(final String domain, final ErrorType errorType) {
        this.domain = domain;
        this.errorType = errorType;
    }

}
