package com.coro.coro.moim.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MoimTagId implements Serializable {

    @Column(name = "moim_id")
    private Long moimId;

    private String name;
}