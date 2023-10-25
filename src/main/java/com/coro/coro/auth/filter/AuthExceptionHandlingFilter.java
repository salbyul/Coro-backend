package com.coro.coro.auth.filter;

import com.coro.coro.auth.exception.AuthException;
import com.coro.coro.common.response.error.ErrorType;
import com.coro.coro.common.response.error.GlobalErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class AuthExceptionHandlingFilter extends GenericFilterBean {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setCharacterEncoding("UTF-8");

            PrintWriter writer = httpResponse.getWriter();
            if (e instanceof AuthException && e.getMessage().equals(ErrorType.AUTH_EXPIRED_ACCESS_TOKEN.getMessage())) {
                writer.print(new ObjectMapper()
                        .writeValueAsString(GlobalErrorResponse.create(ErrorType.AUTH_EXPIRED_ACCESS_TOKEN)));
            } else {
                writer.print(new ObjectMapper()
                        .writeValueAsString(GlobalErrorResponse.create(ErrorType.AUTH_ERROR)));
            }
        }
    }
}
