package com.coro.coro.configuration;

import com.coro.coro.application.annotation.resolver.StatusResolver;
import com.coro.coro.common.annotation.resolver.DateArgumentResolver;
import com.coro.coro.moim.annotation.resolver.SearchArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ArgumentResolverConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SearchArgumentResolver());
        resolvers.add(new StatusResolver());
        resolvers.add(new DateArgumentResolver());
    }
}
