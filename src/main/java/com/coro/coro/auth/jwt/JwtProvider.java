package com.coro.coro.auth.jwt;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface JwtProvider {

    String generateAccessToken(final String userNickname);

    Authentication getAuthentication(final String token);

    String getUserNickname(final String token);

    String extractToken(final HttpServletRequest request);

    boolean isValidToken(final String token);
}
