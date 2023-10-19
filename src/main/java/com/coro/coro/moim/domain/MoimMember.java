package com.coro.coro.moim.domain;

import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.common.response.error.ErrorType;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.moim.dto.request.MoimMemberModificationRequest;
import com.coro.coro.moim.exception.MoimException;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "moim_member")
@AllArgsConstructor
@Builder
public class MoimMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_id")
    private Moim moim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(value = EnumType.STRING)
    private MemberRole role;

    @Override
    public void prePersist() {
        super.prePersist();
        if (this.role == null) {
            this.role = MemberRole.USER;
        }
    }

    public boolean canManage() {
        return !this.role.equals(MemberRole.USER);
    }

    public void update(final MoimMemberModificationRequest moimMemberRequest) {
        if (notEqualsMemberName(moimMemberRequest)) {
            throw new MoimException(ErrorType.MOIM_MEMBER_NOT_VALID);
        }
        this.role = moimMemberRequest.getRole();
        if (this.role == MemberRole.LEADER) {
            this.moim.changeLeader(this.member);
        }
    }

    private boolean notEqualsMemberName(final MoimMemberModificationRequest moimMemberRequest) {
        return !this.member.getNickname().equals(moimMemberRequest.getMemberName());
    }
}
