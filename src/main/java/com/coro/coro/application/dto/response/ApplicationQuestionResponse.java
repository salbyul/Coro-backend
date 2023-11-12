package com.coro.coro.application.dto.response;

import com.coro.coro.application.domain.ApplicationQuestion;
import lombok.Getter;

@Getter
public class ApplicationQuestionResponse {

    private final String content;
    private final Integer order;

    public ApplicationQuestionResponse(final ApplicationQuestion applicationQuestion) {
        this.content = applicationQuestion.getContent();
        this.order = applicationQuestion.getOrder();
    }
}
