package com.coro.coro.member.domain;

import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.exception.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;

class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("asdf@asdf.com")
                .nickname("닉네임")
                .password("asdf1234!@")
                .build();
    }

    @Test
    @DisplayName("중복된 이메일의 경우 예외 발생")
    void duplicatedEmail() {
        Member duplicatedMember = Member.builder()
                .email("asdf@asdf.com")
                .nickname("닉네임2")
                .password("asdf1234!@")
                .build();

        assertThatThrownBy(() ->
                member.verifyDuplication(List.of(duplicatedMember))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_EMAIL_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("중복된 닉네임의 경우 예외 발생")
    void duplicatedNickname() {
        Member duplicatedMember = Member.builder()
                .email("a@a.com")
                .nickname("닉네임")
                .password("asdf1234!@")
                .build();

        assertThatThrownBy(() ->
                member.verifyDuplication(List.of(duplicatedMember))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NICKNAME_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("올바른 회원 수정")
    void changeTo() {
        Member member = Member.builder()
                .email("asdf@asdf.com")
                .nickname("닉네임")
                .password("asdf1234!@")
                .build();
        MemberModificationRequest modificationRequest =
                new MemberModificationRequest("asdf1234!@", "good1234!@", "변경된 회원소개");
        member.changeTo(modificationRequest);

        assertThat(member.getIntroduction()).isEqualTo("변경된 회원소개");
        assertThat(member.getPassword()).isEqualTo("good1234!@");
    }
}