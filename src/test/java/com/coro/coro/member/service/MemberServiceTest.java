package com.coro.coro.member.service;

import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService.register(new MemberRegisterRequest("1@1.com", "asdf1234!@", "123"));
    }

    @Test
    @DisplayName("[회원가입] 정상적인 회원가입")
    void register() {
        memberService.register(new MemberRegisterRequest("a@a.com", "asdf1234!@", "닉네임입니다"));
    }

    @Test
    @DisplayName("[회원가입] 이메일 NULL의 경우")
    void registerFailByEmailNull() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest(null, "asdf1234!@", "이메일널")))
                .isInstanceOf(MemberException.class)
                .hasMessage(EMAIL_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 이메일 빈 문자열의 경우")
    void registerFailByEmailBlank() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("", "asdf1234!@", "이메일빈문자열")))
                .isInstanceOf(MemberException.class)
                .hasMessage(EMAIL_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 이메일 중복")
    void registerFailByEmailDuplication() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("1@1.com", "asdf1234!@", "닉네임입니다")))
                .isInstanceOf(MemberException.class)
                .hasMessage(EMAIL_DUPLICATE.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[회원가입] 이메일 형식이 옳지 않은 경우")
    @ValueSource(strings = {"asdf", "asdf@asdf", "asdf.com"})
    void registerFailByEmailRegex(final String value) {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest(value, "asdf1234!@", "ㅎㅎ")))
                .isInstanceOf(MemberException.class)
                .hasMessage(EMAIL_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 비밀번호 NULL의 경우")
    void registerFailByPasswordNull() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("password@null.com", null, "PSnull")))
                .isInstanceOf(MemberException.class)
                .hasMessage(PASSWORD_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 비밀번호 빈 문자열의 경우")
    void registerFailByPasswordBlank() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("password@null.com", "", "PSnull")))
                .isInstanceOf(MemberException.class)
                .hasMessage(PASSWORD_NULL.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[회원가입] 비밀번호 형식이 옳지 않은 경우")
    @ValueSource(strings = {"asdfasdfas", "asdfasdf12", "asdfasdfas 1 @", "asdfasdfasdfasdfasdfasdfasdfas"})
    void registerFailByPasswordRegex(final String value) {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("good@good.com", value, "nickname")))
                .isInstanceOf(MemberException.class)
                .hasMessage(PASSWORD_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 닉네임 NULL의 경우")
    void registerFailByNicknameNull() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("nickname@null.com", "asdf1234!@", null)))
                .isInstanceOf(MemberException.class)
                .hasMessage(NICKNAME_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 닉네임 빈 문자열의 경우")
    void registerFailByNicknameBlank() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("nickname@null.com", "asdf1234!@", "")))
                .isInstanceOf(MemberException.class)
                .hasMessage(NICKNAME_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 닉네임 중복")
    void registerFailByNicknameDuplication() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("2@2.com", "asdf1234!@", "123")))
                .isInstanceOf(MemberException.class)
                .hasMessage(NICKNAME_DUPLICATE.getMessage());
    }
    @ParameterizedTest
    @DisplayName("[회원가입] 닉네임 형식이 옳지 않은 경우")
    @ValueSource(strings = {"a", "asdfasdfasdfasdf", "닉 네 임"})
    void registerFailByNicknameRegex(final String value) {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("abc@abc.com", "asdf1234!@", value)))
                .isInstanceOf(MemberException.class)
                .hasMessage(NICKNAME_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("[로그인] 정상적인 로그인")
    void login() {
        memberService.login(new MemberLoginRequest("1@1.com", "asdf1234!@"));
    }

    @Test
    @DisplayName("[로그인] 틀린 이메일의 경우")
    void loginFailByEmail() {
        assertThatThrownBy(() -> memberService.login(new MemberLoginRequest("12@2.com", "asdf1234!@")))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("[로그인] 틀린 비밀번호의 경우")
    void loginFailByPassword() {
        assertThatThrownBy(() -> memberService.login(new MemberLoginRequest("asdf@asdf.com", "asdf1234!#")))
                .isInstanceOf(MemberException.class);
    }
}