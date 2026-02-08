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
                .description("JWT 인증 + OAuth 소셜 로그인 + AI 어시스턴트 (GPT-4o-mini) + 세션 관리\n\n"
                        + "**주요 기능:**\n"
                        + "- 🔐 JWT 토큰 기반 인증\n"
                        + "- 🌐 OAuth 2.0 소셜 로그인 (Google, Kakao, Naver)\n"
                        + "- 🤖 3D 엔지니어링 AI 어시스턴트\n"
                        + "- 💾 세션 기반 대화 관리 (PostgreSQL)\n"
                        + "- 📚 4가지 3D 모델 지원 (Drone, Robot Arm, Robot Gripper, Suspension)\n\n"
                        + "**v2.1.0 업데이트 (2026-02-08):**\n"
                        + "- ✅ AI 채팅 세션 관리 (날짜 기반 세션 ID: yyyyMMdd-NNN)\n"
                        + "- ✅ 대화 히스토리 DB 저장 (chat_sessions, chat_messages 테이블)\n"
                        + "- ✅ 토큰 폭탄 방지 (최근 20개 대화만 OpenAI 전달)\n"
                        + "- ✅ 세션 소유권 검증 강화 (403 Forbidden)\n"
                        + "- ✅ 트랜잭션 처리 (OpenAI 실패 시 롤백)\n"
                        + "- ❌ 구형 API 삭제 (POST /api/ai/ask, GET /api/ai/history 등)")
                .version("2.1.0")
                .contact(new Contact()
                        .name("SIMVEX Development Team")
                        .email("dev@simvex.com")
                        .url("https://github.com/simvex/platform"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
}
