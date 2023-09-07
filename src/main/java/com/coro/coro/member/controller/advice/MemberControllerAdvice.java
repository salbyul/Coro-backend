package com.coro.coro.member.controller.advice;

import com.coro.coro.common.response.error.DomainErrorResponse;
import com.coro.coro.common.response.error.GlobalErrorResponse;
import com.coro.coro.member.exception.MemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MemberControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MemberException.class)
    public DomainErrorResponse memberException(final MemberException e) {
        return DomainErrorResponse.create(e.getDomain(), e.getErrorType());
    }

    /*
    Member 도메인에서 확인되지 않은 예외
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public GlobalErrorResponse unknownException(final RuntimeException e) {
        e.printStackTrace();
        return GlobalErrorResponse.create();
    }
}
