package com.coro.coro.member.domain;

import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.mock.FakePasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberTest {

    private final PasswordEncoder passwordEncoder = new FakePasswordEncoder();

    private static final String EMAIL = "asdf@asdf.com";
    private static final String PASSWORD = "asdf1234!@";
    private static final String NICKNAME = "닉네임";
    private static final String INTRODUCTION = "자기소개입니다.";
    private static final MemberState STATE = MemberState.ACTIVE;


    @Test
    @DisplayName("패스워드 암호화 확인")
    void encryptPassword() {
        Member member = generateMember();

        member.encryptPassword(passwordEncoder);

        assertThat(passwordEncoder.matches(PASSWORD, member.getPassword())).isTrue();
    }

    @Test
    @DisplayName("이메일과 닉네임 중복 검사 확인 성공")
    void verifyDuplication() {
        Member member = generateMember();
        Member notDuplicatedMember = Member.builder()
                .id(2L)
                .email("a@a.com")
                .password(PASSWORD)
                .nickname("닉네임1")
                .introduction(INTRODUCTION)
                .moimList(new ArrayList<>())
                .state(STATE)
                .build();

        member.verifyDuplication(List.of(notDuplicatedMember));
    }

    @Test
    @DisplayName("이메일과 닉네임 중복 검사 확인 실패 - 이메일 중복의 경우")
    void verifyDuplicationFailByDuplicatedEmail() {
        Member member = generateMember();
        Member duplicatedEmailMember = Member.builder()
                .id(2L)
                .email(EMAIL)
                .password(PASSWORD)
                .nickname("닉네임1")
                .introduction(INTRODUCTION)
                .moimList(new ArrayList<>())
                .state(STATE)
                .build();

        assertThatThrownBy(() ->
                member.verifyDuplication(List.of(duplicatedEmailMember))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("이메일과 닉네임 중복 검사 확인 실패 - 닉네임 중복의 경우")
    void verifyDuplicationFailByDuplicatedNickname() {
        Member member = generateMember();
        Member duplicatedEmailMember = Member.builder()
                .id(2L)
                .email("a@a.com")
                .password(PASSWORD)
                .nickname(NICKNAME)
                .introduction(INTRODUCTION)
                .moimList(new ArrayList<>())
                .state(STATE)
                .build();

        assertThatThrownBy(() ->
                member.verifyDuplication(List.of(duplicatedEmailMember))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_DUPLICATE_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("성공적인 회원 수정")
    void update() {
        Member member = generateMember();
        member.encryptPassword(passwordEncoder);

        MemberModificationRequest requestMember = new MemberModificationRequest(passwordEncoder.encode(PASSWORD), "qwer0987!@", "변경된 자기소개입니다.");
        member.update(requestMember, passwordEncoder);

        assertAll(
                () -> assertThat(member.getIntroduction()).isEqualTo(requestMember.getIntroduction()),
                () -> assertThat(passwordEncoder.matches(requestMember.getNewPassword(), member.getPassword())).isTrue()
        );
    }

    @Test
    @DisplayName("회원수정 실패 - 비밀번호가 다를 경우")
    void updateFailByNotValidPassword() {
        Member member = generateMember();
        member.encryptPassword(passwordEncoder);

        assertThatThrownBy(() ->
            member.update(
                    new MemberModificationRequest("vhjs3857#*", "qwer0987!@", "변경된 자기소개입니다."),
                    passwordEncoder
            )
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NOT_VALID_PASSWORD.getMessage());
    }

    public Member generateMember() {
        return Member.builder()
                .id(1L)
                .email(EMAIL)
                .password(PASSWORD)
                .nickname(NICKNAME)
                .introduction(INTRODUCTION)
                .moimList(new ArrayList<>())
                .state(STATE)
                .build();
    }
}