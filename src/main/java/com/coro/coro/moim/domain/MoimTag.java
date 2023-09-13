package com.coro.coro.moim.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoimTag implements Persistable<MoimTagId> {

    @EmbeddedId
    private MoimTagId id;

    @MapsId("moimId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_id")
    private Moim moim;

    public static MoimTag generateMoimTag(final String tagName, final Moim moim) {
        return new MoimTag(tagName, moim.getId(), moim);
    }

    private MoimTag(final String tagName, final Long moimId, final Moim moim) {
        this.id = new MoimTagId(moimId, tagName);
        this.moim = moim;
    }

    @Override
    public MoimTagId getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true; // 태그 변경 시 모든 태그 제거 후 추가이기 때문
    }

    public String getName() {
        return id.getName();
    }

    public boolean isDuplicateName(final MoimTag moimTag) {
        return this.getName().equals(moimTag.getName());
    }
}