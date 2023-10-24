package com.coro.coro.schedule.controller.advice;

import com.coro.coro.common.response.error.DomainErrorResponse;
import com.coro.coro.common.response.error.GlobalErrorResponse;
import com.coro.coro.schedule.exception.ScheduleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.coro.coro.schedule")
public class ScheduleControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ScheduleException.class)
    public DomainErrorResponse moimException(final ScheduleException e) {
        return DomainErrorResponse.create(e.getDomain(), e.getErrorType());
    }

    /* Schedule 도메인에서 확인되지 않은 예외 */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public GlobalErrorResponse unknownException(final Exception e) {
        log.error("", e);
        return GlobalErrorResponse.create();
    }
}
