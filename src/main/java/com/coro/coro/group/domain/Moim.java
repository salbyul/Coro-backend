package com.coro.coro.group.domain;

import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.member.domain.Member;

import javax.persistence.*;

@Entity
public class Moim extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member leader;

    private String name;
    private String introduction;
    private Boolean visible;

    @Enumerated(EnumType.STRING)
    private MoimType type;

    private String address;

    @Enumerated(EnumType.STRING)
    private MoimState state;

    @Override
    public void prePersist() {
        super.prePersist();
        if (this.introduction == null) {
            this.introduction = "우리 모임을 소개해주세요.";
        }
        this.state = MoimState.ACTIVE;
    }
}