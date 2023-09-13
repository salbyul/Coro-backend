package com.coro.coro.auth.exception;

import com.coro.coro.common.exception.GlobalException;
import com.coro.coro.common.response.error.ErrorType;

public class AuthException extends GlobalException {

    private static final String AUTH = "Auth";

    public AuthException(final ErrorType errorType) {
        super(AUTH, errorType);
    }
}
