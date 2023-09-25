package com.coro.coro.moim.domain.id;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MoimTagId implements Serializable {

    @Column(name = "moim_id")
    private Long moimId;

    @Column(nullable = false)
    private String name;

    public MoimTagId(final String name) {
        this.name = name;
    }
}