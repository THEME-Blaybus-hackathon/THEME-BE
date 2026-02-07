package com.example.Project.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.example.Project.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final JwtProperties properties;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;

        String secretKey = properties.getSecret();

        // Validate JWT secret
        if (secretKey == null || secretKey.isBlank()) {
            log.error("❌ JWT secret is not configured!");
            throw new IllegalStateException(
                    "JWT_SECRET is missing. Please configure it in environment variables."
            );
        }

        // Initialize key with proper error handling
        byte[] keyBytes;
        try {
            // Try to decode as Base64
            keyBytes = Decoders.BASE64.decode(secretKey);
            log.info("✅ JWT secret loaded successfully (Base64)");
        } catch (Exception e) {
            log.warn("⚠️ JWT secret is not Base64. Using raw key.");
            keyBytes = secretKey.getBytes();

            // Ensure key is at least 256 bits (32 bytes) for HS256
            if (keyBytes.length < 32) {
                byte[] paddedKey = new byte[32];
                System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
                keyBytes = paddedKey;
                log.info("✅ Key padded to 32 bytes");
            }
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access Token 생성
    public String createAccessToken(String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + properties.getAccessTokenValidity());

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + properties.getRefreshTokenValidity());

        return Jwts.builder()
                .setSubject(email)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Token에서 이메일 추출
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // Token에서 권한 추출
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    // Token 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    // Claims 추출
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
