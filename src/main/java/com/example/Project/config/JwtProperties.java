package com.example.Project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 Properties
 *
 * application.yml의 jwt.* 속성을 바인딩합니다.
 *
 * @ConfigurationProperties를 사용하여 타입 안전하게 설정을 관리합니다.
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 서명용 Secret Key 환경변수: JWT_SECRET
     */
    private String secret;

    /**
     * Access Token 유효시간 (밀리초) 기본값: 3600000ms = 1시간 환경변수:
     * JWT_ACCESS_TOKEN_VALIDITY
     */
    private long accessTokenValidity = 3600000L;

    /**
     * Refresh Token 유효시간 (밀리초) 기본값: 604800000ms = 7일 환경변수:
     * JWT_REFRESH_TOKEN_VALIDITY
     */
    private long refreshTokenValidity = 604800000L;

    // Getter & Setter
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(long accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public long getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(long refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }
}
