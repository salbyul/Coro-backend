package com.coro.coro.application.validator;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.exception.ApplicationException;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;

public class ApplicationQuestionValidator {

    private static final int MAX_NAME_LENGTH = 200;

    public static void validateApplicationQuestion(final List<ApplicationQuestion> applicationQuestionList) {
        if (applicationQuestionList.size() == 0) {
            return;
        }
        validateContent(applicationQuestionList);
        validateOrders(applicationQuestionList);
    }

    private static void validateContent(final List<ApplicationQuestion> applicationQuestionList) {
        boolean isGreaterThanMaxLength = applicationQuestionList.stream().anyMatch(question -> question.getContent().length() > MAX_NAME_LENGTH);
        boolean isEmpty = applicationQuestionList.stream().anyMatch(question -> isEmpty(question.getContent()));
        if (isGreaterThanMaxLength || isEmpty) {
            throw new ApplicationException(APPLICATION_QUESTION_NOT_VALID_CONTENT);
        }
    }

    private static boolean isEmpty(final String value) {
        return !StringUtils.hasText(value);
    }

    private static void validateOrders(final List<ApplicationQuestion> applicationQuestionList) {
        if (applicationQuestionList.size() > 10) {
            throw new ApplicationException(APPLICATION_QUESTION_GREATER_THAN_MAX);
        }
        int expectedMaxOrder = applicationQuestionList.size();
        boolean canContinue = true;
        while (expectedMaxOrder != 0 && canContinue) {
            canContinue = false;
            for (ApplicationQuestion applicationQuestion : applicationQuestionList) {
                if (applicationQuestion.getOrder() == expectedMaxOrder) {
                    canContinue = true;
                    expectedMaxOrder--;
                    break;
                }
            }
        }
        if (expectedMaxOrder != 0) {
            throw new ApplicationException(APPLICATION_QUESTION_NOT_VALID_ORDERS);
        }
    }
}