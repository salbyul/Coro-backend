package com.coro.coro.application.domain;

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
public class ApplicationQuestion extends BaseEntity implements Persistable<Long> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orders", nullable = false)
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_id")
    private Moim moim;

    private String content;

    public ApplicationQuestion(final Moim moim, final String content, final Integer order) {
        this.moim = moim;
        this.content = content;
        this.order = order;
    }

    public static ApplicationQuestion generate(final Moim moim, final ApplicationQuestionRegisterRequest requestQuestion) {
        return new ApplicationQuestion(moim, requestQuestion.getContent(), requestQuestion.getOrder());
    }

    public int getOrder() {
        return this.order;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
