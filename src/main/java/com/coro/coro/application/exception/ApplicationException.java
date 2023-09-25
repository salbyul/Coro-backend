package com.coro.coro.application.exception;

import com.coro.coro.common.exception.GlobalException;
import com.coro.coro.common.response.error.ErrorType;

public class ApplicationException extends GlobalException {

    private static final String APPLICATION = "Application";

    public ApplicationException(final ErrorType errorType) {
        super(APPLICATION, errorType);
    }
}
