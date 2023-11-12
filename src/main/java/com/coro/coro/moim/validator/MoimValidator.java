package com.coro.coro.moim.validator;

import com.coro.coro.common.annotation.Validator;
import com.coro.coro.moim.domain.Moim;
import com.coro.coro.moim.domain.MoimType;
import com.coro.coro.moim.exception.MoimException;
import com.coro.coro.moim.service.port.MoimRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.util.StringUtils;

import static com.coro.coro.common.response.error.ErrorType.*;

@Validator
@AllArgsConstructor
@Builder
public class MoimValidator {

    private final MoimRepository moimRepository;

    public void validateMoim(final Moim moim) {
        validateName(moim.getName());
        validateIntroduction(moim.getIntroduction());
        validateType(moim.getType());
        validateVisible(moim.getVisible());
    }

    private void validateName(final String name) {
        if (isEmpty(name)) {
            throw new MoimException(MOIM_NAME_NULL);
        }
        if (name.length() > 30) {
            throw new MoimException(MOIM_NOT_VALID_NAME);
        }
    }

    private boolean isEmpty(final String name) {
        return !StringUtils.hasText(name);
    }

    private void validateIntroduction(final String introduction) {
        if (isEmpty(introduction)) {
            return;
        }
        if (introduction.length() > 500) {
            throw new MoimException(MOIM_NOT_VALID_INTRODUCTION);
        }
    }

    private void validateType(final MoimType type) {
        if (type == null) {
            throw new MoimException(MOIM_TYPE_NOT_VALID);
        }
    }

    private void validateVisible(final Boolean visible) {
        if (visible == null) {
            throw new MoimException(MOIM_VISIBLE_NULL);
        }
    }

    public void validateDuplicateName(final String name) {
        moimRepository.findByName(name)
                .ifPresent(moim -> {
                    throw new MoimException(MOIM_DUPLICATE_NAME);
                });
    }
}