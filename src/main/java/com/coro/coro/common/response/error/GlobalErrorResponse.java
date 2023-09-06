package com.coro.coro.common.response.error;

import com.coro.coro.common.response.CoroResponse;
import lombok.Getter;

import static com.coro.coro.common.response.error.ErrorType.*;

@Getter
public class GlobalErrorResponse extends CoroResponse {

    private final String code;
    private final String message;

    protected GlobalErrorResponse() {
        this.message = UNKNOWN.getMessage();
        this.code = UNKNOWN.getCode();
    }

    protected GlobalErrorResponse(final ErrorType errorType) {
        this.message = errorType.getMessage();
        this.code = errorType.getCode();
    }

    public static GlobalErrorResponse create() {
        return new GlobalErrorResponse();
    }
}