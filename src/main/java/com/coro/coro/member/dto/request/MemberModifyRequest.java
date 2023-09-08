package com.coro.coro.member.dto.request;

import lombok.Getter;

@Getter
public class MemberModifyRequest {

    private String originalPassword;
    private String newPassword;
    private String introduction;
}
