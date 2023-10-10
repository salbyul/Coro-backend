package com.coro.coro.application.domain;

import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.member.domain.Member;
import com.coro.coro.moim.domain.Moim;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_id")
    private Moim moim;

    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus status;

    private Application(final Member member, final Moim moim) {
        this.member = member;
        this.moim = moim;
        this.status = ApplicationStatus.WAIT;
    }

    public static Application generate(final Member member, final Moim moim) {
        return new Application(member, moim);
    }

    public void updateStatusTo(final ApplicationStatus status) {
        this.status = status;
    }

    public boolean isWait() {
        return this.status.equals(ApplicationStatus.WAIT);
    }
}
