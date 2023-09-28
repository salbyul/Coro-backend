package com.coro.coro.application.dto.response;

import com.coro.coro.application.domain.ApplicationQuestion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationQuestionResponse {

    private String content;
    private Integer order;

    public ApplicationQuestionResponse(final ApplicationQuestion applicationQuestion) {
        this.content = applicationQuestion.getContent();
        this.order = applicationQuestion.getOrder();
    }
}
