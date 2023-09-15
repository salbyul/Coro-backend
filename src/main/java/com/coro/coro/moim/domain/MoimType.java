package com.coro.coro.moim.domain;

import com.coro.coro.common.response.error.ErrorType;
import com.coro.coro.moim.exception.MoimException;

import java.util.Arrays;

public enum MoimType {
    MIXED("mixed"), FACE_TO_FACE("faceToFace"), NON_CONTACT("nonContact");

    private final String type;

    MoimType(final String type) {
        this.type = type;
    }

    public static MoimType getType(final String type) {
        return Arrays.stream(values()).filter(moimType -> moimType.type.equals(type))
                .findAny()
                .orElseThrow(() -> new MoimException(ErrorType.MOIM_TYPE_NULL));
    }
}
