package com.coro.coro.application.domain;

import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_answer")
@AllArgsConstructor
@Builder
public class ApplicationAnswer implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    private String question;

    private String content;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isNew() { // 제출한 답변은 수정 불가
        return true;
    }
}
