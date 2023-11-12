package com.coro.coro.member.dto.response;

import com.coro.coro.member.domain.Member;
import lombok.Getter;

@Getter
public class MemberInformationResponse {

    private final String email;
    private final String nickname;

    public MemberInformationResponse(final Member member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
    }
}
