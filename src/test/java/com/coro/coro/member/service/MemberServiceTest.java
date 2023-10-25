package com.coro.coro.member.service;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.mock.FakeContainer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberServiceTest {

    private static final String EXAMPLE_EMAIL = "asdf@asdf.com";
    private static final String EXAMPLE_PASSWORD = "asdf1234!@";
    private static final String EXAMPLE_NICKNAME = "123";

    @Test
    @DisplayName("[회원가입] 정상적인 회원가입")
    void register() {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedId = container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, EXAMPLE_NICKNAME));

//        검증
        Member member = container.memberRepository.findById(savedId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        assertAll(
                () -> assertThat(member.getEmail()).isEqualTo(EXAMPLE_EMAIL),
                () -> assertThat(container.passwordEncoder.matches(EXAMPLE_PASSWORD, member.getPassword())).isTrue(),
                () -> assertThat(member.getNickname()).isEqualTo(EXAMPLE_NICKNAME)
        );
    }

    @Test
    @DisplayName("[회원가입] 이메일 NULL의 경우")
    void registerFailByEmailNull() {
        FakeContainer container = new FakeContainer();

//        회원가입
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest(null, EXAMPLE_PASSWORD, EXAMPLE_NICKNAME))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_EMAIL_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 이메일 빈 문자열의 경우")
    void registerFailByEmailBlank() {
        FakeContainer container = new FakeContainer();

//        회원가입
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest("", EXAMPLE_PASSWORD, EXAMPLE_NICKNAME))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_EMAIL_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 이메일 중복")
    void registerFailByEmailDuplication() {
        FakeContainer container = new FakeContainer();

//        회원가입
        container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, EXAMPLE_NICKNAME));

//        검증
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, "닉네임2"))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_DUPLICATE_EMAIL.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[회원가입] 이메일 형식이 옳지 않은 경우")
    @ValueSource(strings = {"asdf", "asdf@asdf", "asdf.com"})
    void registerFailByEmailRegex(final String value) {
        FakeContainer container = new FakeContainer();

//        회원가입
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest(value, EXAMPLE_PASSWORD, EXAMPLE_NICKNAME))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NOT_VALID_EMAIL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 비밀번호 NULL의 경우")
    void registerFailByPasswordNull() {
        FakeContainer container = new FakeContainer();

//        회원가입
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, null, EXAMPLE_NICKNAME))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_PASSWORD_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 비밀번호 빈 문자열의 경우")
    void registerFailByPasswordBlank() {
        FakeContainer container = new FakeContainer();

//        회원가입
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, "", EXAMPLE_NICKNAME))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_PASSWORD_NULL.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[회원가입] 비밀번호 형식이 옳지 않은 경우")
    @ValueSource(strings = {"asdfasdfas", "asdfasdf12", "asdfasdfas 1 @", "asdfasdfasdfasdfasdfasdfasdfas"})
    void registerFailByPasswordRegex(final String value) {
        FakeContainer container = new FakeContainer();

//        회원가입
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, value, EXAMPLE_NICKNAME))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NOT_VALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 닉네임 NULL의 경우")
    void registerFailByNicknameNull() {
        FakeContainer container = new FakeContainer();

//        회원가입
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, null))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NICKNAME_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 닉네임 빈 문자열의 경우")
    void registerFailByNicknameBlank() {
        FakeContainer container = new FakeContainer();

//        회원가입
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, ""))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NICKNAME_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 닉네임 중복")
    void registerFailByNicknameDuplication() {
        FakeContainer container = new FakeContainer();

//        회원가입
        container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, EXAMPLE_NICKNAME));

//        검증
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest("a@a.com", EXAMPLE_PASSWORD, EXAMPLE_NICKNAME))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_DUPLICATE_NICKNAME.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[회원가입] 닉네임 형식이 옳지 않은 경우")
    @ValueSource(strings = {"a", "asdfasdfasdfasdf", "닉 네 임"})
    void registerFailByNicknameRegex(final String value) {
        FakeContainer container = new FakeContainer();

//        회원가입
        assertThatThrownBy(() ->
                container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, value))
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NOT_VALID_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("[회원수정] 정상적인 회원 수정")
    void updateMember() {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedId = container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, EXAMPLE_NICKNAME));
        Member member = container.memberRepository.findById(savedId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

//        회원수정
        MemberModificationRequest requestMember =
                new MemberModificationRequest(EXAMPLE_PASSWORD, "qwer1234!@", "바뀐 소개입니다.");
        container.memberService.update(member.getId(), requestMember);

//        검증
        Member updatedMember = container.memberRepository.findById(member.getId()).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        boolean matchPassword = container.passwordEncoder.matches(requestMember.getNewPassword(), updatedMember.getPassword());

        assertThat(matchPassword).isTrue();
        assertThat(updatedMember.getIntroduction()).isEqualTo(requestMember.getIntroduction());
    }

    @Test
    @DisplayName("[회원수정] 틀린 비밀번호의 경우")
    void updateFailByPassword() {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long savedId = container.memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, EXAMPLE_NICKNAME));

//        회원 수정
        Member member = container.memberRepository.findById(savedId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        MemberModificationRequest requestMember = new MemberModificationRequest("1234", "qwer1234!@", "바뀐 소개입니다.");

        assertThatThrownBy(() ->
                container.memberService.update(member.getId(), requestMember)
        )
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NOT_VALID_PASSWORD.getMessage());
    }
}