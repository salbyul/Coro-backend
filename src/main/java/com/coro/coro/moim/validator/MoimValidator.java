package com.coro.coro.moim.validator;

import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.exception.MoimException;
import org.springframework.util.StringUtils;

import static com.coro.coro.common.response.error.ErrorType.*;

public class MoimValidator {

    public static void validateMoim(final Moim moim) {
        validateName(moim.getName());
        validateIntroduction(moim.getIntroduction());
        validateVisible(moim.getVisible());
    }

    private static void validateName(final String name) {
        if (isEmpty(name)) {
            throw new MoimException(MOIM_NAME_NULL);
        }
        if (name.length() > 30) {
            throw new MoimException(MOIM_NAME_NOT_VALID);
        }
    }

    private static boolean isEmpty(final String name) {
        return !StringUtils.hasText(name);
    }

    private static void validateIntroduction(final String introduction) {
        if (isEmpty(introduction)) {
            return;
        }
        if (introduction.length() > 500) {
            throw new MoimException(MOIM_INTRODUCTION_NOT_VALID);
        }
    }

    private static void validateVisible(final Boolean visible) {
        if (visible == null) {
            throw new MoimException(MOIM_VISIBLE_NULL);
        }
    }
}