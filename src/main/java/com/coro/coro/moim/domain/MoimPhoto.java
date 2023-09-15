package com.coro.coro.moim.domain;

import javax.persistence.*;

@Table(name = "moim_photo")
@Entity
public class MoimPhoto {

    @Id
    @Column(name = "moim_id")
    private Long moimId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "moim_id")
    private Moim moim;

    @Column(name = "original_name")
    private String originalName;

    private String name;
}