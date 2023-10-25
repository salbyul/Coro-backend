package com.coro.coro.auth.filter;

import com.coro.coro.auth.exception.AuthException;
import com.coro.coro.auth.jwt.JwtProviderImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String REFRESH_TOKEN_PATH = "/api/auth/new";
    private final JwtProviderImpl tokenProvider;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        String token = tokenProvider.extractToken(request);

        if (token != null && !request.getRequestURI().equals(REFRESH_TOKEN_PATH)) {
            if (!tokenProvider.isValidToken(token)) {
                throw new AuthException(AUTH_EXPIRED_ACCESS_TOKEN);
            }
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
