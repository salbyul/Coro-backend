package com.coro.coro.moim.exception;

import com.coro.coro.common.exception.GlobalException;
import com.coro.coro.common.response.error.ErrorType;

public class MoimException extends GlobalException {

    private static final String MOIM = "Moim";

    public MoimException(final ErrorType errorType) {
        super(MOIM, errorType);
    }
}
