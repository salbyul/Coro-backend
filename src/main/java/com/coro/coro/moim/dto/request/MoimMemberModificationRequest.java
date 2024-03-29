package com.coro.coro.moim.dto.request;

import com.coro.coro.member.domain.MemberRole;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoimMemberModificationRequest {

    private Long id;
    private String memberName;
    private MemberRole role;
}
