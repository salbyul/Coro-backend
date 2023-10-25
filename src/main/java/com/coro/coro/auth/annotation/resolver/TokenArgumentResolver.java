package com.coro.coro.auth.annotation.resolver;

import com.coro.coro.auth.annotation.TokenSet;
import com.coro.coro.auth.dto.request.TokenSetRequest;
import com.coro.coro.auth.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.coro.coro.common.response.error.ErrorType.*;

@Slf4j
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(TokenSetRequest.class) && parameter.hasParameterAnnotation(TokenSet.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String refreshToken = request.getHeader("refresh");

        if (Objects.isNull(refreshToken)) {
            throw new AuthException(AUTH_NOT_VALID_TOKEN);
        }

        return new TokenSetRequest(refreshToken);
    }
}
