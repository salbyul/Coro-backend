package com.coro.coro.moim.domain;

import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Table(name = "moim_tag")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class MoimTag implements Persistable<Long> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_id")
    private Moim moim;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true; // 태그 변경 시 모든 태그 제거 후 추가이기 때문
    }

    public boolean isDuplicateName(final MoimTag moimTag) {
        return this.getName().equals(moimTag.getName());
    }
}