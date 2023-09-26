package com.coro.coro.application.domain;

import com.coro.coro.application.domain.id.ApplicationQuestionId;
import com.coro.coro.application.dto.request.ApplicationQuestionRegisterRequest;
import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.moim.domain.Moim;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_question")
public class ApplicationQuestion extends BaseEntity implements Persistable<ApplicationQuestionId> {

    @EmbeddedId
    private ApplicationQuestionId id;

    @MapsId("moimId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_id")
    private Moim moim;

    private String content;

    public ApplicationQuestion(final Moim moim, final String content, final Integer order) {
        this.moim = moim;
        this.content = content;
        this.id = new ApplicationQuestionId(order);
    }

    public static ApplicationQuestion generateApplicationQuestion(final Moim moim, final ApplicationQuestionRegisterRequest requestQuestion) {
        return new ApplicationQuestion(moim, requestQuestion.getContent(), requestQuestion.getOrder());
    }

    public int getOrder() {
        return this.id.getOrder();
    }

    @Override
    public ApplicationQuestionId getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
