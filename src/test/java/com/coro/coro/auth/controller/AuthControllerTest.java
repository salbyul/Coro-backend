package com.coro.coro.auth.controller;

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

import static com.coro.coro.common.response.error.ErrorType.*;
import static org.assertj.core.api.Assertions.*;

class AuthControllerTest {

    @Test
    @DisplayName("새로운 토큰 발행")
    void newTokenResponse() {
        FakeContainer container = new FakeContainer();

        Long memberId = container.memberService.register(new MemberRegisterRequest("asdf@asdf.com", "asdf1234!@", "닉네임"));
        TokenResponse tokenResponse = container.memberService.login(new MemberLoginRequest("asdf@asdf.com", "asdf1234!@"));

        Member member = container.memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(MEMBER_NOT_FOUND));

        TokenSetRequest tokenSetRequest = new TokenSetRequest(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
        APIResponse response = container.authController.newTokenResponse(tokenSetRequest);
        TokenResponse token = (TokenResponse) response.getBody().get("token");

        assertThat(token.getAccessToken()).isEqualTo("activeAccessToken" + member.getNickname());
        assertThat(token.getRefreshToken()).isEqualTo("uid".repeat(30));
    }

    @Test
    @DisplayName("새로운 토큰 발행 - 기존 토큰이 없을 경우")
    void newTokenResponseFailByNotExistToken() {
        FakeContainer container = new FakeContainer();

        TokenSetRequest tokenSetRequest = new TokenSetRequest("notExistAccessToken", "notExistRefreshToken");

        assertThatThrownBy(() ->
                container.authController.newTokenResponse(tokenSetRequest)
        )
                .isInstanceOf(AuthException.class)
                .hasMessage(AUTH_TOKEN_NOT_FOUND.getMessage());
    }
}