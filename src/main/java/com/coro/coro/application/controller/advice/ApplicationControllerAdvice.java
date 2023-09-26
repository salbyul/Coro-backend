package com.coro.coro.application.controller.advice;

import com.coro.coro.application.exception.ApplicationException;
import com.coro.coro.common.response.error.DomainErrorResponse;
import com.coro.coro.common.response.error.GlobalErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.coro.coro.application")
public class ApplicationControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApplicationException.class)
    public DomainErrorResponse applicationException(final ApplicationException e) {
        return DomainErrorResponse.create(e.getDomain(), e.getErrorType());
    }

    /* Application 도메인에서 확인되지 않은 예외 */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public GlobalErrorResponse unknownException(final Exception e) {
        log.error("", e);
        return GlobalErrorResponse.create();
    }
}