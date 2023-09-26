package com.coro.coro.moim.domain;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.member.domain.Member;
import com.coro.coro.moim.dto.request.MoimModifyRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "moim")
    private List<MoimTag> tagList = new ArrayList<>();

    @OneToMany(mappedBy = "moim")
    private List<ApplicationQuestion> questionList = new ArrayList<>();

    @Builder
    public Moim(final Member leader, final String name, final String introduction, final Boolean visible, final MoimType type) {
        this.leader = leader;
        this.name = name;
        this.introduction = introduction;
        this.visible = visible;
        this.type = type;
    }

    @Override
    public void prePersist() {
        super.prePersist();
        if (isEmpty(this.introduction)) {
            this.introduction = "우리 모임을 소개해주세요.";
        }
        this.state = MoimState.ACTIVE;
    }

    private boolean isEmpty(final String value) {
        return value == null || value.equals("");
    }

    public void changeTo(final MoimModifyRequest requestMoim) {
        this.name = requestMoim.getName();
        this.introduction = requestMoim.getIntroduction();
        this.type = MoimType.getType(requestMoim.getType());
        this.visible = requestMoim.getVisible();
    }
}