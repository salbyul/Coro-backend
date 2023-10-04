package com.coro.coro.moim.dto.request;

import com.coro.coro.member.domain.MemberRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MoimMemberModificationRequest {

    private Long id;
    private String memberName;
    private MemberRole role;
}
