package com.coro.coro.common.annotation.resolver;

import com.coro.coro.common.annotation.Date;
import com.coro.coro.common.exception.ArgumentResolverException;
import com.coro.coro.common.response.error.ErrorType;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

public class DateArgumentResolver implements HandlerMethodArgumentResolver {

    private final static String DATE = "DATE";

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(LocalDate.class) && parameter.hasParameterAnnotation(Date.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String date = request.getParameter("date");
        validateDateFormat(date);
        String[] split = date.split("-");
        return LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    private void validateDateFormat(final String date) {
        String regex = "[0-9]+-[0-9]+-[0-9]+";
        if (!date.matches(regex)) {
            throw new ArgumentResolverException(DATE, ErrorType.WRONG_ARGUMENT);
        }
    }
}
