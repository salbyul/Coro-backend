package com.coro.coro.common.advice;

import com.coro.coro.common.exception.ArgumentResolverException;
import com.coro.coro.common.response.error.DomainErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.coro.coro")
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ArgumentResolverException.class)
    public DomainErrorResponse memberException(final ArgumentResolverException e) {
        return DomainErrorResponse.create(e.getDomain(), e.getErrorType());
    }
}
