package com.example.crazyjava.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        String schemeName = "Authorization";
        SecurityScheme tokenHeader = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(schemeName)
                .description("值为登录接口返回的 token，不要加 Bearer");
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(schemeName, tokenHeader))
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}
