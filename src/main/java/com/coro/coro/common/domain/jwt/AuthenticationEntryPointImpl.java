package com.coro.coro.common.domain.jwt;

import com.coro.coro.common.response.error.ErrorType;
import com.coro.coro.common.response.error.GlobalErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    /* 인증 중에 예외가 발생하면 잘못된 토큰으로 인식하고 ErrorResponse를 전송한다. */
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        writer.print(new ObjectMapper()
                .writeValueAsString(GlobalErrorResponse.create(ErrorType.AUTH_ERROR)));
    }
}
