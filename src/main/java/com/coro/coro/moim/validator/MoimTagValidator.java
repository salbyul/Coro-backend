package com.coro.coro.moim.validator;

import com.coro.coro.moim.domain.MoimTag;
import com.coro.coro.moim.exception.MoimException;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;

public class MoimTagValidator {

    public static void validateTag(final List<MoimTag> tagList) {
        validateEmpty(tagList);
        validateDuplicate(tagList);
        validateValidTag(tagList);
    }

    private static void validateEmpty(final List<MoimTag> tagList) {
        boolean existsNull = tagList.stream().anyMatch(tag -> !StringUtils.hasText(tag.getName()));
        if (existsNull) {
            throw new MoimException(MOIM_TAG_NULL);
        }
    }

    private static void validateDuplicate(final List<MoimTag> tagList) {
        for (int i = 0; i < tagList.size(); i++) {
            MoimTag moimTag = tagList.get(i);

            for (int j = i + 1; j < tagList.size(); j++) {
                MoimTag target = tagList.get(j);
                if (moimTag.isDuplicateName(target)) {
                    throw new MoimException(MOIM_TAG_DUPLICATE);
                }
            }
        }
    }

    private static void validateValidTag(final List<MoimTag> tagList) {
        String regex = "[a-zA-Z0-9ㄱ-ㅎ가-힣]{1,10}";
        boolean isValid = tagList.stream().anyMatch(tag -> !tag.getName().matches(regex));
        if (isValid) {
            throw new MoimException(MOIM_TAG_NOT_VALID);
        }
    }
}
