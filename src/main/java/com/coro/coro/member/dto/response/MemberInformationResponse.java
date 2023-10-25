package com.coro.coro.member.dto.response;

import com.coro.coro.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInformationResponse {

    private String email;
    private String nickname;

    public MemberInformationResponse(final Member member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
    }
}
