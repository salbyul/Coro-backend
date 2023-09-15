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

    MEMBER_EMAIL_NULL("110", "Email 값이 비어있습니다."),
    MEMBER_EMAIL_DUPLICATE("111", "Email 값이 중복됩니다."),
    MEMBER_EMAIL_NOT_VALID("112", "Email 값의 형태가 올바르지 않습니다."),

    MEMBER_PASSWORD_NULL("120", "Password 값이 비어있습니다."),
    MEMBER_PASSWORD_NOT_VALID("122", "Password 값의 형태가 올바르지 않습니다."),

    MEMBER_NICKNAME_NULL("130", "Nickname 값이 비어있습니다."),
    MEMBER_NICKNAME_DUPLICATE("131", "Nickname 값이 중복됩니다."),
    MEMBER_NICKNAME_NOT_VALID("132", "Nickname 값의 형태가 올바르지 않습니다."),

    //    Moim
    MOIM_NOT_FOUND("200", "해당 Moim이 없습니다."),

    MOIM_NAME_NULL("210", "Name 값이 비어있습니다."),
    MOIM_NAME_DUPLICATE("211", "Name 값이 중복됩니다."),
    MOIM_NAME_NOT_VALID("212", "Name 값의 형태가 올바르지 않습니다."),

    MOIM_INTRODUCTION_NOT_VALID("222", "Introduction 값의 형태가 올바르지 않습니다."),

    MOIM_VISIBLE_NULL("230", "Visible 값이 비어있습니다."),

    MOIM_TAG_NULL("240", "Tag 값이 비어있습니다."),
    MOIM_TAG_DUPLICATE("241", "Tag 값이 중복됩니다."),
    MOIM_TAG_NOT_VALID("242", "Tag 값의 형태가 올바르지 않습니다."),

    MOIM_TYPE_NULL("250", "Type 값이 비어있습니다.");

    private final String code;
    private final String message;
    }
