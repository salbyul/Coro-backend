package com.coro.coro.application.dto.response;

import com.coro.coro.application.domain.ApplicationAnswer;
import lombok.Getter;

@Getter
public class ApplicationAnswerDTO {

    private final String question;
    private final String answer;

    public ApplicationAnswerDTO(final ApplicationAnswer applicationAnswer) {
        this.question = applicationAnswer.getQuestion();
        this.answer = applicationAnswer.getContent();
    }
}
