package com.coro.coro.moim.dto.response;

import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.moim.domain.MoimMember;
import lombok.Getter;

@Getter
public class MoimMemberResponse {

    private final Long id;
    private final String memberName;
    private final MemberRole role;

    public MoimMemberResponse(final MoimMember moimMember) {
        this.id = moimMember.getId();
        this.memberName = moimMember.getMember().getNickname();
        this.role = moimMember.getRole();
    }
}
