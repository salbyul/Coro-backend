package com.coro.coro.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberStateTest {

    @Test
    @DisplayName("isActive 메서드 검증")
    void isActive() {
        assertThat(MemberState.SUSPENDED.isActive()).isFalse();
        assertThat(MemberState.ACTIVE.isActive()).isTrue();
    }

    @Test
    @DisplayName("isSuspended 메서드 검증")
    void isSuspended() {
        assertThat(MemberState.SUSPENDED.isSuspended()).isTrue();
        assertThat(MemberState.ACTIVE.isSuspended()).isFalse();
    }
}