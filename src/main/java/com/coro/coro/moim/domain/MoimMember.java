package com.coro.coro.moim.domain;

import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.common.response.error.ErrorType;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.domain.MemberRole;
import com.coro.coro.moim.dto.request.MoimMemberModificationRequest;
import com.coro.coro.moim.dto.request.MoimMemberRequest;
import com.coro.coro.moim.exception.MoimException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "moim_member")
public class MoimMember extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private MoimMember(final Moim moim, final Member member, final MemberRole role) {
        this.moim = moim;
        this.member = member;
        this.role = role;
    }

    public static MoimMember generate(final Moim moim, final Member member) {
        return new MoimMember(moim, member, MemberRole.LEADER);
    }

    public boolean canManage() {
        return !this.role.equals(MemberRole.USER);
    }

    public void update(final MoimMemberModificationRequest moimMemberRequest) {
        if (notEqualsMemberName(moimMemberRequest)) {
            throw new MoimException(ErrorType.MOIM_MEMBER_NOT_VALID);
        }
        this.role = moimMemberRequest.getRole();
    }

    private boolean notEqualsMemberName(final MoimMemberModificationRequest moimMemberRequest) {
        return !this.member.getNickname().equals(moimMemberRequest.getMemberName());
    }
}
