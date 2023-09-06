package com.coro.coro.common.response.error;

import lombok.Getter;

@Getter
public class DomainErrorResponse extends GlobalErrorResponse {

    private final String domain;

    protected DomainErrorResponse(final String domain, final ErrorType errorType) {
        super(errorType);
        this.domain = domain;
    }

    public static DomainErrorResponse create(final String domain, final ErrorType errorType) {
        return new DomainErrorResponse(domain, errorType);
    }
}
