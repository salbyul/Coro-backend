package com.coro.coro.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_photo")
public class MemberPhoto {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "original_name")
    private String originalName;

    private String name;

    public MemberPhoto(final Member member, final String originalName, final String name) {
        this.member = member;
        this.originalName = originalName;
        this.name = name;
    }
}