package com.coro.coro.moim.domain;

import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.member.domain.Member;
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
    private MoimRole role;

    @Override
    public void prePersist() {
        super.prePersist();
        if (this.role == null) {
            this.role = MoimRole.USER;
        }
    }

    private MoimMember(final Moim moim, final Member member, final MoimRole role) {
        this.moim = moim;
        this.member = member;
        this.role = role;
    }

    public static MoimMember generate(final Moim moim, final Member member) {
        return new MoimMember(moim, member, MoimRole.LEADER);
    }

    public boolean canManage() {
        return !this.role.equals(MoimRole.USER);
    }
}
