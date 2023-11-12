package com.coro.coro.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberModificationRequest {

    private String originalPassword;
    private String newPassword;
}
