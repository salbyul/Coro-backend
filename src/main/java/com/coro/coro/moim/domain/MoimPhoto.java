package com.coro.coro.moim.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Table(name = "moim_photo")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoimPhoto {

    @Id
    @Column(name = "moim_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "moim_id")
    private Moim moim;

    @Column(name = "original_name")
    private String originalName;

    private String name;

    public MoimPhoto(final Moim moim, final String originalName, final String name) {
        this.moim = moim;
        this.originalName = originalName;
        this.name = name;
    }
}