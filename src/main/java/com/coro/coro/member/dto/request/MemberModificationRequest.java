package com.coro.coro.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberModificationRequest {

    private String originalPassword;
    private String newPassword;
}
