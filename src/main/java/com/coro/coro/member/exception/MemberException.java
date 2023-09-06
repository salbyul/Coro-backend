package com.coro.coro.member.exception;

import com.coro.coro.common.exception.GlobalException;
import com.coro.coro.common.response.error.ErrorType;

public class MemberException extends GlobalException {

    private static final String MEMBER = "Member";

    public MemberException(final ErrorType errorType) {
        super(MEMBER, errorType);
    }
}
