package com.coro.coro.common.exception;

import com.coro.coro.common.response.error.ErrorType;

public class ArgumentResolverException extends GlobalException {

    public ArgumentResolverException(final String domain, final ErrorType errorType) {
        super(domain, errorType);
    }
}
