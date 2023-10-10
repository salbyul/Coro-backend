package com.coro.coro.member.service;

import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberModificationRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.member.exception.MemberException;
import com.coro.coro.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    private static final String EXAMPLE_EMAIL = "1@1.com";
    private static final String EXAMPLE_PASSWORD = "asdf1234!@";
    private static final String EXAMPLE_NICKNAME = "123";
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    private Member member;

    @BeforeEach
    void setUp() {
        memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, EXAMPLE_NICKNAME));
        member = memberRepository.findByEmail(EXAMPLE_EMAIL)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }

    @Test
    @DisplayName("[회원가입] 정상적인 회원가입")
    void register() {
        memberService.register(new MemberRegisterRequest("a@a.com", EXAMPLE_PASSWORD, "닉네임입니다"));
    }

    @Test
    @DisplayName("[회원가입] 이메일 NULL의 경우")
    void registerFailByEmailNull() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest(null, EXAMPLE_PASSWORD, "이메일널")))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_EMAIL_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 이메일 빈 문자열의 경우")
    void registerFailByEmailBlank() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("", EXAMPLE_PASSWORD, "이메일빈문자열")))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_EMAIL_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 이메일 중복")
    void registerFailByEmailDuplication() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD, "닉네임입니다")))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_EMAIL_DUPLICATE.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[회원가입] 이메일 형식이 옳지 않은 경우")
    @ValueSource(strings = {"asdf", "asdf@asdf", "asdf.com"})
    void registerFailByEmailRegex(final String value) {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest(value, EXAMPLE_PASSWORD, "ㅎㅎ")))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_EMAIL_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 비밀번호 NULL의 경우")
    void registerFailByPasswordNull() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("password@null.com", null, "PSnull")))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_PASSWORD_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 비밀번호 빈 문자열의 경우")
    void registerFailByPasswordBlank() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("password@null.com", "", "PSnull")))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_PASSWORD_NULL.getMessage());
    }

    @ParameterizedTest
    @DisplayName("[회원가입] 비밀번호 형식이 옳지 않은 경우")
    @ValueSource(strings = {"asdfasdfas", "asdfasdf12", "asdfasdfas 1 @", "asdfasdfasdfasdfasdfasdfasdfas"})
    void registerFailByPasswordRegex(final String value) {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("good@good.com", value, "nickname")))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_PASSWORD_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 닉네임 NULL의 경우")
    void registerFailByNicknameNull() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("nickname@null.com", EXAMPLE_PASSWORD, null)))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NICKNAME_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 닉네임 빈 문자열의 경우")
    void registerFailByNicknameBlank() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("nickname@null.com", EXAMPLE_PASSWORD, "")))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NICKNAME_NULL.getMessage());
    }

    @Test
    @DisplayName("[회원가입] 닉네임 중복")
    void registerFailByNicknameDuplication() {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("2@2.com", EXAMPLE_PASSWORD, EXAMPLE_NICKNAME)))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NICKNAME_DUPLICATE.getMessage());
    }
    @ParameterizedTest
    @DisplayName("[회원가입] 닉네임 형식이 옳지 않은 경우")
    @ValueSource(strings = {"a", "asdfasdfasdfasdf", "닉 네 임"})
    void registerFailByNicknameRegex(final String value) {
        assertThatThrownBy(() -> memberService.register(new MemberRegisterRequest("abc@abc.com", EXAMPLE_PASSWORD, value)))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NICKNAME_NOT_VALID.getMessage());
    }

    @Test
    @DisplayName("[로그인] 정상적인 로그인")
    void login() {
        memberService.login(new MemberLoginRequest(EXAMPLE_EMAIL, EXAMPLE_PASSWORD));
    }

    @Test
    @DisplayName("[로그인] 틀린 이메일의 경우")
    void loginFailByEmail() {
        assertThatThrownBy(() -> memberService.login(new MemberLoginRequest("12@2.com", EXAMPLE_PASSWORD)))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("[로그인] 틀린 비밀번호의 경우")
    void loginFailByPassword() {
        assertThatThrownBy(() -> memberService.login(new MemberLoginRequest("asdf@asdf.com", "asdf1234!#")))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("[회원수정] 정상적인 회원 수정")
    void updateMember() {
        MemberModificationRequest requestMember = new MemberModificationRequest(EXAMPLE_PASSWORD, "qwer1234!@", "바뀐 소개입니다.");
        memberService.update(member.getId(), requestMember);

        assertThat(passwordEncoder.matches(requestMember.getNewPassword(), member.getPassword())).isTrue();
        assertThat(member.getIntroduction()).isEqualTo(requestMember.getIntroduction());
    }

    @Test
    @DisplayName("[회원수정] 틀린 비밀번호의 경우")
    void updateFailByPassword() {
        MemberModificationRequest requestMember = new MemberModificationRequest("1234", "qwer1234!@", "바뀐 소개입니다.");
        assertThatThrownBy(() -> memberService.update(1L, requestMember))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
    }
}