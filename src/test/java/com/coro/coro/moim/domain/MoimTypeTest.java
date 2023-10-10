package com.coro.coro.moim.domain;

import com.coro.coro.moim.exception.MoimException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;

class MoimTypeTest {

    @Test
    @DisplayName("올바른 타입을 찾는 경우")
    void validGetTypeMethod() {
        assertThat(MoimType.getType("faceToFace")).isEqualTo(MoimType.FACE_TO_FACE);
        assertThat(MoimType.getType("mixed")).isEqualTo(MoimType.MIXED);
        assertThat(MoimType.getType("nonContact")).isEqualTo(MoimType.NON_CONTACT);
    }

    @Test
    @DisplayName("유효하지 않는 타입을 찾는 경우 예외 발생")
    void notValidGetTypeMethod() {
        assertThatThrownBy(() -> MoimType.getType("wrong Type"))
                .isInstanceOf(MoimException.class)
                .hasMessage(MOIM_TYPE_NOT_VALID.getMessage());
    }
}