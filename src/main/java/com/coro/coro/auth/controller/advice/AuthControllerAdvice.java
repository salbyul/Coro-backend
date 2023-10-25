package com.coro.coro.auth.controller.advice;

import com.coro.coro.auth.exception.AuthException;
import com.coro.coro.common.response.error.DomainErrorResponse;
import com.coro.coro.common.response.error.GlobalErrorResponse;
import com.coro.coro.member.exception.MemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.coro.coro.auth")
public class AuthControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthException.class)
    public DomainErrorResponse memberException(final AuthException e) {
        return DomainErrorResponse.create(e.getDomain(), e.getErrorType());
    }

    /* 확인되지 않은 예외 */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public GlobalErrorResponse unknownException(final Exception e) {
        log.error("", e);
        return GlobalErrorResponse.create();
    }
}
