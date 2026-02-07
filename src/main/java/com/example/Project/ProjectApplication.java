package com.example.Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ProjectApplication {

    public static void main(String[] args) {
        // Load .env file only in local development
        // Docker/Render uses system environment variables
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // .env 파일이 없어도 에러 발생 안함
                    .load();

            // Set environment variables from .env (local development only)
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );
        } catch (Exception e) {
            // Docker/Render: .env file not available, use system env vars
            System.out.println("Running without .env file (using system environment variables)");
        }

        SpringApplication.run(ProjectApplication.class, args);
    }

}
