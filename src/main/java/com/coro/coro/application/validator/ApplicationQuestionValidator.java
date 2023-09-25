package com.coro.coro.application.validator;

import com.coro.coro.application.domain.ApplicationQuestion;
import com.coro.coro.application.exception.ApplicationException;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;

public class ApplicationQuestionValidator {

    private static final int MAX_NAME_LENGTH = 200;

    public static void validateApplicationQuestion(final List<ApplicationQuestion> applicationQuestions) {
        if (applicationQuestions.size() == 0) {
            return;
        }
        validateContent(applicationQuestions);
        validateOrders(applicationQuestions);
    }

    private static void validateContent(final List<ApplicationQuestion> applicationQuestions) {
        boolean isGreaterThanMaxLength = applicationQuestions.stream().anyMatch(question -> question.getContent().length() > MAX_NAME_LENGTH);
        boolean isEmpty = applicationQuestions.stream().anyMatch(question -> isEmpty(question.getContent()));
        if (isGreaterThanMaxLength || isEmpty) {
            throw new ApplicationException(APPLICATION_CONTENT_VALID);
        }
    }

    private static boolean isEmpty(final String value) {
        return !StringUtils.hasText(value);
    }

    private static void validateOrders(final List<ApplicationQuestion> applicationQuestions) {
        if (applicationQuestions.size() > 10) {
            throw new ApplicationException(APPLICATION_MAX);
        }
        int expectedMaxOrder = applicationQuestions.size();
        boolean canContinue = true;
        while (expectedMaxOrder != 0 && canContinue) {
            canContinue = false;
            for (ApplicationQuestion applicationQuestion : applicationQuestions) {
                if (applicationQuestion.getOrder() == expectedMaxOrder) {
                    canContinue = true;
                    expectedMaxOrder--;
                    break;
                }
            }
        }
        if (expectedMaxOrder != 0) {
            throw new ApplicationException(APPLICATION_ORDERS_VALID);
        }
    }
}