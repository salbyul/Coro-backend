package com.coro.coro.auth.service;

import com.coro.coro.auth.dto.request.TokenSetRequest;
import com.coro.coro.auth.dto.response.TokenResponse;
import com.coro.coro.auth.exception.AuthException;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.mock.FakeContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;

class AuthServiceTest {

    @Test
    @DisplayName("리프레시 토큰 확인 후 새로운 토큰 발행")
    void issueNewTokenSet() {
        FakeContainer container = new FakeContainer();

//        회원가입
        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(MEMBER_NOT_FOUND));

//        로그인 토큰 생성
        TokenResponse existToken = container.memberService.login(new MemberLoginRequest("asdf@asdf.com", "asdf1234!@"));

//        새로운 토큰 발행
        TokenResponse tokenResponse = container.authService.issueNewTokenSet(new TokenSetRequest(existToken.getAccessToken(), existToken.getRefreshToken()));

//        검증
        assertThat(tokenResponse.getAccessToken()).isEqualTo("activeAccessToken" + member.getNickname());
        assertThat(tokenResponse.getRefreshToken()).isEqualTo("uid".repeat(30));
    }

    @Test
    @DisplayName("리프레시 토큰 확인 후 새로운 토큰 발행 - 기존 토큰이 없을 경우")
    void issueNewTokenSetFailByNotExistToken() {
        FakeContainer container = new FakeContainer();

        assertThatThrownBy(() ->
                container.authService.issueNewTokenSet(new TokenSetRequest("notExistAccessToken", "notExistRefreshToken"))
        )
                .isInstanceOf(AuthException.class)
                .hasMessage(AUTH_TOKEN_NOT_FOUND.getMessage());
    }
}