package com.coro.coro.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(swaggerInfo());
    }

    private Info swaggerInfo() {
        return new Info()
                .title("Coro 서버 API문서")
                .description("Coro 서버의 API 문서입니다.")
                .version("v0.0.1");
    }
}
