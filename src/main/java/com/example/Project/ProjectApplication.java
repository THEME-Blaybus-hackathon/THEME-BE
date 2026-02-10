package com.example.Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.github.cdimascio.dotenv.Dotenv;

@EnableJpaAuditing // ★ [필수] JPA Auditing 기능 활성화
@SpringBootApplication
public class ProjectApplication {

    public static void main(String[] args) {
        loadEnv(); // .env 로드
        SpringApplication.run(ProjectApplication.class, args);
    }

    // 기존 환경변수 로드 로직 유지
    private static void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                if (System.getProperty(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
            
            System.out.println("✅ Environment variables loaded from .env file");
        } catch (Exception e) {
            System.out.println("ℹ️ Running with system environment variables (No .env file found)");
        }
    }
}