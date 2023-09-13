package com.coro.coro.moim.controller.advice;

import com.coro.coro.common.response.error.DomainErrorResponse;
import com.coro.coro.common.response.error.GlobalErrorResponse;
import com.coro.coro.moim.exception.MoimException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.coro.coro.moim")
public class MoimControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MoimException.class)
    public DomainErrorResponse moimException(final MoimException e) {
        return DomainErrorResponse.create(e.getDomain(), e.getErrorType());
    }

    /* Moim 도메인에서 확인되지 않은 예외 */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public GlobalErrorResponse unknownException(final RuntimeException e) {
        log.error("", e);
        return GlobalErrorResponse.create();
    }
}
