package com.coro.coro.application.domain.id;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationQuestionId implements Serializable {

    @Column(name = "moim_id")
    private Long moimId;

    @Column(name = "orders", nullable = false)
    private Integer order;

    public ApplicationQuestionId(final Integer order) {
        this.order = order;
    }
}
