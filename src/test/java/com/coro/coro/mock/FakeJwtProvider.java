package com.coro.coro.mock;

import com.coro.coro.auth.jwt.JwtProvider;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public class FakeJwtProvider implements JwtProvider {

    private final String ACCESS_TOKEN = "activeAccessToken";
    private final String EXPIRE_ACCESS_TOKEN = "expiredToken";

    @Override
    public String generateAccessToken(final String userNickname) {
        return ACCESS_TOKEN + userNickname;
    }

    @Override
    @Deprecated
    public Authentication getAuthentication(final String token) {
        return null;
    }

    @Override
    public String getUserNickname(final String token) {
        return token.substring(ACCESS_TOKEN.length() - 1);
    }

    @Override
    @Deprecated
    public String extractToken(final HttpServletRequest request) {
        return null;
    }

    @Override
    public boolean isValidToken(final String token) {
        return !token.equals(EXPIRE_ACCESS_TOKEN);
    }

    public String expireToken(final String token) {
        if (!token.contains(ACCESS_TOKEN)) {
            throw new IllegalArgumentException("Not Valid Fake Access Token!!");
        }
        return EXPIRE_ACCESS_TOKEN;
    }
}
