package com.coro.coro.common.response.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    UNKNOWN("000", "정의되지 않은 에러입니다."),

    //    Auth
    AUTH_ERROR("001", "인증 에러입니다."),

    //    Member
    MEMBER_NOT_FOUND("100", "해당 Member가 없습니다."),

    EMAIL_NULL("110", "Email 값이 비어있습니다."),
    EMAIL_DUPLICATE("111", "Email 값이 중복됩니다."),
    EMAIL_NOT_VALID("112", "Email 값의 형태가 올바르지 않습니다."),

    PASSWORD_NULL("120", "Password 값이 비어있습니다."),
    PASSWORD_NOT_VALID("122", "Password 값의 형태가 올바르지 않습니다."),

    NICKNAME_NULL("130", "Nickname 값이 비어있습니다."),
    NICKNAME_DUPLICATE("131", "Nickname 값이 중복됩니다."),
    NICKNAME_NOT_VALID("132", "Nickname 값의 형태가 올바르지 않습니다.");

    private final String code;
    private final String message;
}
