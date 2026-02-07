package com.example.Project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // [1] 시큐리티 필터를 거치지 않게 하여 403 에러를 원천 차단합니다.
        return (web) -> web.ignoring()
            .requestMatchers("/h2-console/**", "/favicon.ico", "/css/**", "/js/**", "/images/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // [2] H2 콘솔은 CSRF 토큰을 지원하지 않으므로 비활성화가 필수입니다.
            .csrf(csrf -> csrf.disable()) 
            
            // [3] H2 콘솔은 <iframe>을 사용하므로 같은 출처에서의 접근을 허용해야 합니다.
            .headers(headers -> headers.frameOptions(f -> f.sameOrigin()))
            
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/login", "/h2-console/**", 
                    "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**",
                    "/api/auth/**", "/api/objects/**"
                ).permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}