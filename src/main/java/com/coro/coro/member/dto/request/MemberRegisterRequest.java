package com.coro.coro.member.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberRegisterRequest {

    private String email;
    private String password;
    private String nickname;
}
