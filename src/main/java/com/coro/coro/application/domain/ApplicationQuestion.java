package com.coro.coro.application.domain;

import com.coro.coro.common.domain.BaseEntity;
import com.coro.coro.moim.domain.Moim;
import lombok.*;
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

    @Builder
    public ApplicationQuestion(final Long id, final Integer order, final Moim moim, final String content) {
        this.id = id;
        this.order = order;
        this.moim = moim;
        this.content = content;
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
