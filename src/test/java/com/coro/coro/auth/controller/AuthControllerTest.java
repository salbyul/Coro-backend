package com.coro.coro.auth.controller;

import com.coro.coro.auth.domain.RefreshToken;
import com.coro.coro.auth.dto.request.TokenSetRequest;
import com.coro.coro.auth.dto.response.TokenResponse;
import com.coro.coro.auth.exception.AuthException;
import com.coro.coro.common.response.APIResponse;
import com.coro.coro.member.domain.Member;
import com.coro.coro.member.dto.request.MemberLoginRequest;
import com.coro.coro.member.dto.request.MemberRegisterRequest;
import com.coro.coro.mock.FakeContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;

class AuthControllerTest {

    @Test
    @DisplayName("로그인 성공")
    void login() {
        FakeContainer container = new FakeContainer();

//        회원가입
        MemberRegisterRequest request = new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임");
        container.memberController.register(request);

//        로그인
        APIResponse loginResponse = container.authController.login(new MemberLoginRequest("asdf@asdf.com", "asdf1234!@"));
        TokenResponse token = (TokenResponse) loginResponse.getBody().get("token");

        assertThat(token.getAccessToken()).isEqualTo("activeAccessToken" + "닉네임");
        assertThat(token.getRefreshToken()).isEqualTo("uid".repeat(30));
    }

    @Test
    @DisplayName("새로운 토큰 발행")
    void newTokenResponse() {
        FakeContainer container = new FakeContainer();

        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        TokenResponse tokenResponse = container.authService.login(new MemberLoginRequest("asdf@asdf.com", "asdf1234!@"));

        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(MEMBER_NOT_FOUND));

        TokenSetRequest tokenSetRequest = new TokenSetRequest(tokenResponse.getRefreshToken());
        APIResponse response = container.authController.issueNewTokenResponse(tokenSetRequest);
        TokenResponse token = (TokenResponse) response.getBody().get("token");

        assertThat(token.getAccessToken()).isEqualTo("activeAccessToken" + member.getNickname());
        assertThat(token.getRefreshToken()).isEqualTo("uid".repeat(30));
    }

    @Test
    @DisplayName("새로운 토큰 발행 - 기존 토큰이 없을 경우")
    void newTokenResponseFailByNotExistToken() {
        FakeContainer container = new FakeContainer();

        TokenSetRequest tokenSetRequest = new TokenSetRequest("notExistRefreshToken");

        assertThatThrownBy(() ->
                container.authController.issueNewTokenResponse(tokenSetRequest)
        )
                .isInstanceOf(AuthException.class)
                .hasMessage(AUTH_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그아웃")
    void logout() {
        FakeContainer container = new FakeContainer();

//        리프레시 토큰 생성
        String refreshToken = "refreshToken";

        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .nickname("nickname")
                .build();
        container.refreshTokenRepository.save(token);

//        로그아웃
        TokenSetRequest tokenSetRequest = new TokenSetRequest(refreshToken);
        container.authController.logout(tokenSetRequest);

//        검증
        Optional<RefreshToken> optionalRefreshToken = container.refreshTokenRepository.findById("refreshToken:" + refreshToken);

        assertThat(optionalRefreshToken.isPresent()).isFalse();
    }
}