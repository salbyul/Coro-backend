package com.coro.coro.member.util;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.coro.coro.member.util.MemberValidator.*;
import static org.assertj.core.api.Assertions.*;

class MemberValidatorTest {

    @Test
    @DisplayName("이메일이 null일 경우")
    void nullEmail() {
        assertThatThrownBy(() -> validateEmail(null))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("이메일이 빈 문자열일 경우")
    void emptyEmail() {
        assertThatThrownBy(() -> validateEmail(""))
                .isInstanceOf(MemberException.class);
    }

    @ParameterizedTest
    @DisplayName("이메일 형식이 아닐 경우")
    @ValueSource(strings = {"asdf", "asdf.com", "asdf@asdf", "@.com"})
    void emailRegex(final String value) {
        assertThatThrownBy(() -> validateEmail(value))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("비밀번호가 null일 경우")
    void nullPassword() {
        assertThatThrownBy(() -> validatePassword(null))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("비밀번호가 빈 문자열일 경우")
    void emptyPassword() {
        assertThatThrownBy(() -> validatePassword(""))
                .isInstanceOf(MemberException.class);
    }

    @ParameterizedTest
    @DisplayName("비밀번호 형식이 아닐 경우")
    @ValueSource(strings = {"asdfasdfa", "asdfasdfasdfasdfasdfasdfasdfas", "asdfasdfasdf", "asdf1234asdf", "asdf 1234 !@#$"})
    void passwordRegex(final String value) {
        assertThatThrownBy(() -> validatePassword(value))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("닉네임이 null일 경우")
    void nullNickname() {
        assertThatThrownBy(() -> validateNickname(null))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("닉네임이 빈 문자열일 경우")
    void emptyNickname() {
        assertThatThrownBy(() -> validateNickname(""))
                .isInstanceOf(MemberException.class);
    }

    @ParameterizedTest
    @DisplayName("닉네임 형식이 아닐 경우")
    @ValueSource(strings = {"a", "asdfasdfasdfasdf", "나는 공백포함이야", "asdf!"})
    void nicknameRegex(final String value) {
        assertThatThrownBy(() -> validateNickname(value))
                .isInstanceOf(MemberException.class);
    }

    @Test
    void memberBuilderTest() {
        assertThatThrownBy(
                () -> Member.builder()
                        .email("")
                        .nickname("")
                        .password("")
                        .build()
        )
                .isInstanceOf(MemberException.class);
    }
}