package com.coro.coro.moim.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Table(name = "moim_photo")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class MoimPhoto {

    @Id
    @Column(name = "moim_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Moim moim;

    @Column(name = "original_name")
    private String originalName;

    private String name;

    @Column(name = "content_type")
    private String contentType;
}