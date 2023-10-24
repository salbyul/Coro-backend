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
    MEMBER_DUPLICATE_EMAIL("111", "Email 값이 중복됩니다."),
    MEMBER_NOT_VALID_EMAIL("112", "Email 값의 형태가 올바르지 않습니다."),

    MEMBER_PASSWORD_NULL("120", "Password 값이 비어있습니다."),
    MEMBER_NOT_VALID_PASSWORD("122", "Password 값의 형태가 올바르지 않습니다."),

    MEMBER_NICKNAME_NULL("130", "Nickname 값이 비어있습니다."),
    MEMBER_DUPLICATE_NICKNAME("131", "Nickname 값이 중복됩니다."),
    MEMBER_NOT_VALID_NICKNAME("132", "Nickname 값의 형태가 올바르지 않습니다."),

    MEMBER_PHOTO_NOT_FOUND("140", "해당 MemberPhoto가 없습니다."),
    MEMBER_PHOTO_NOT_VALID("142", "MemberPhoto가 유효하지 않습니다."),

    //    Moim
    MOIM_NOT_FOUND("200", "해당 Moim이 없습니다."),
    MOIM_FORBIDDEN("201", "권한이 없습니다."),

    MOIM_NAME_NULL("210", "Name 값이 비어있습니다."),
    MOIM_DUPLICATE_NAME("211", "Name 값이 중복됩니다."),
    MOIM_NOT_VALID_NAME("212", "Name 값의 형태가 올바르지 않습니다."),

    MOIM_NOT_VALID_INTRODUCTION("222", "Introduction 값의 형태가 올바르지 않습니다."),

    MOIM_VISIBLE_NULL("230", "Visible 값이 비어있습니다."),

    MOIM_TAG_NULL("240", "Tag 값이 비어있습니다."),
    MOIM_TAG_DUPLICATE("241", "Tag 값이 중복됩니다."),
    MOIM_TAG_NOT_VALID("242", "Tag 값의 형태가 올바르지 않습니다."),

    MOIM_TYPE_NOT_VALID("252", "Type 값이 올바르지 않습니다."),

    MOIM_PHOTO_NOT_FOUND("260", "해당 MoimPhoto가 없습니다."),
    MOIM_PHOTO_NOT_VALID("262", "MoimPhoto가 유효하지 않습니다."),

    //    Application
    APPLICATION_NOT_FOUND("300", "해당 Application이 없습니다."),
    APPLICATION_FORBIDDEN("303", "권한이 없습니다."),
    APPLICATION_ALREADY_EXIST("306", "이미 지원한 모임입니다."),
    APPLICATION_EXIST_MEMBER("307", "이미 가입한 모임입니다."),
    APPLICATION_ANSWER_NOT_COMPLETE("308", "답변이 불완전합니다."),
    APPLICATION_QUESTION_GREATER_THAN_MAX("309", "Application Question은 총 10개까지만 생성이 가능합니다."),
    APPLICATION_QUESTION_NOT_VALID_CONTENT("312", "Content 값의 형태가 올바르지 않습니다."),
    APPLICATION_QUESTION_NOT_VALID_ORDERS("321", "Orders 값이 올바르지 않습니다."),
    APPLICATION_STATUS_NOT_VALID("332", "Status 값이 올바르지 않습니다."),
    APPLICATION_STATUS_ALREADY("339", "이미 처리된 Application 입니다."),

    //    MoimMember
    MOIM_MEMBER_NOT_FOUND("400", "해당 MoimMember가 없습니다."),
    MOIM_MEMBER_NOT_VALID("401", "MoimMember가 유효하지 않습니다."),
    MOIM_MEMBER_FORBIDDN("403", "권한이 없습니다."),

    //    Schedule
    SCHEDULE_NOT_FOUND("500", "해당 Schedule이 없습니다."),
    SCHEDULE_NOT_VALID_TITLE("511", "Title 값이 올바르지 않습니다."),
    SCHEDULE_NOT_VALID_CONTENT("521", "Content 값이 올바르지 않습니다.");

    private final String code;
    private final String message;
    }
