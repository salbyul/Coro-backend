package com.coro.coro.application.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_answer")
public class ApplicationAnswer implements Persistable<Long> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private ApplicationAnswer(final Application application, final ApplicationQuestion applicationQuestion, final String content) {
        this.application = application;
        this.question = applicationQuestion.getContent();
        this.content = content;
    };

    public static ApplicationAnswer generate(final Application application, final ApplicationQuestion applicationQuestion, final String content) {
        return new ApplicationAnswer(application, applicationQuestion, content);
    }
}
