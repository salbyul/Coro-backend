package com.coro.coro.moim.domain;

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
                .orElseThrow(RuntimeException::new); // 발생할 수 없는 예외
    }
}
