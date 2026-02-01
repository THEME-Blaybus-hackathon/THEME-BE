package com.example.Project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
                .title("🚀 SIMVEX Platform API")
                .description("JWT 인증 + OAuth 소셜 로그인 + AI 어시스턴트 (GPT-5-mini)\n\n"
                        + "**주요 기능:**\n"
                        + "- 🔐 JWT 토큰 기반 인증\n"
                        + "- 🌐 OAuth 2.0 소셜 로그인 (Google, Kakao, Naver)\n"
                        + "- 🤖 3D 엔지니어링 AI 어시스턴트\n"
                        + "- 📚 4가지 3D 모델 지원 (Jet Engine, Suspension, Robot Arm, Vice)")
                .version("2.0.0")
                .contact(new Contact()
                        .name("SIMVEX Development Team")
                        .email("dev@simvex.com")
                        .url("https://github.com/simvex/platform"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
}
