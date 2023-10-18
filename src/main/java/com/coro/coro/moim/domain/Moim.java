package com.coro.coro.moim.domain;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.member.domain.Member;
import com.coro.coro.moim.dto.request.MoimModificationRequest;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "moim")
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

    @Enumerated(EnumType.STRING)
    private MoimState state;

    @Builder.Default
    @OneToMany(mappedBy = "moim")
    private List<MoimTag> tagList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "moim")
    private List<ApplicationQuestion> questionList = new ArrayList<>();

    @Override
    public void prePersist() {
        super.prePersist();
        if (!StringUtils.hasText(this.introduction)) {
            this.introduction = "우리 모임을 소개해주세요.";
        }
        this.state = MoimState.ACTIVE;
    }

    public void update(final MoimModificationRequest requestMoim) {
        this.name = requestMoim.getName();
        this.introduction = requestMoim.getIntroduction();
        this.visible = requestMoim.getVisible();
        this.type = MoimType.getType(requestMoim.getType());
    }

    public void changeLeader(final Member newLeader) {
        this.leader = newLeader;
    }

    public void changeQuestionListTo(final List<ApplicationQuestion> applicationQuestionList) {
        this.questionList = applicationQuestionList;
    }

    public void changeMoimTagListTo(final List<MoimTag> tagList) {
        this.tagList = tagList;
    }
}