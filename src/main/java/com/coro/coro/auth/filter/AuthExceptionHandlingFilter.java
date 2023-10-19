package com.coro.coro.auth.filter;

import com.coro.coro.common.response.error.ErrorType;
import com.coro.coro.common.response.error.GlobalErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthExceptionHandlingFilter extends GenericFilterBean {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setCharacterEncoding("UTF-8");

            PrintWriter writer = httpResponse.getWriter();
            writer.print(new ObjectMapper()
                    .writeValueAsString(GlobalErrorResponse.create(ErrorType.AUTH_ERROR)));
        }
    }
}
