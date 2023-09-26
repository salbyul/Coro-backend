package com.coro.coro.moim.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Table(name = "moim_tag")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoimTag implements Persistable<Long> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_id")
    private Moim moim;

    public static MoimTag generateMoimTag(final String tagName, final Moim moim) {
        return new MoimTag(tagName, moim);
    }

    private MoimTag(final String tagName, final Moim moim) {
        this.name = tagName;
        this.moim = moim;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true; // 태그 변경 시 모든 태그 제거 후 추가이기 때문
    }

    public String getName() {
        return this.name;
    }

    public boolean isDuplicateName(final MoimTag moimTag) {
        return this.getName().equals(moimTag.getName());
    }
}