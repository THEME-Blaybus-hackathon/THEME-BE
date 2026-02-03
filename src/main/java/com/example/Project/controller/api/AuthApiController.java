package com.example.Project.controller.api;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.ApiResponse;
import com.example.Project.dto.LoginRequest;
import com.example.Project.dto.SignupRequest;
import com.example.Project.dto.TokenRefreshRequest;
import com.example.Project.dto.TokenResponse;
import com.example.Project.dto.UserResponse;
import com.example.Project.entity.User;
import com.example.Project.helper.constants.SocialLoginType;
import com.example.Project.repository.UserRepository;
import com.example.Project.security.JwtTokenProvider;
import com.example.Project.service.OauthService;
import com.example.Project.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "인증 API", description = "JWT 기반 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final OauthService oauthService;
    private final UserService userService;

    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하여 JWT 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        // 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 사용자 정보 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // JWT 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();

        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "로그인 성공"));
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token 발급")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_TOKEN", "유효하지 않은 리프레시 토큰입니다."));
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();

        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "토큰 갱신 성공"));
    }

    @Operation(summary = "소셜 로그인 (OAuth)", description = "Google, Kakao, Naver 소셜 로그인으로 JWT 토큰 발급")
    @GetMapping("/{provider}")
    public void oauthLogin(
            @PathVariable(name = "provider") SocialLoginType provider,
            HttpServletResponse response,
            HttpSession session) throws IOException {
        // API 타입임을 세션에 표시
        session.setAttribute("oauth_type", "api");

        // OAuth 제공자의 인증 URL로 리다이렉트
        String redirectUrl = oauthService.getRedirectUrl(provider);
        response.sendRedirect(redirectUrl);
    }

    @Operation(summary = "회원가입", description = "이메일/비밀번호로 신규 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request) {
        try {
            UserResponse user = userService.signup(request);
            return ResponseEntity.ok(ApiResponse.success(user, "회원가입 성공"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("SIGNUP_FAILED", e.getMessage()));
        }
    }

    @Operation(
            summary = "로그아웃",
            description = "로그아웃 처리. 클라이언트는 저장된 JWT 토큰을 삭제해야 합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // JWT는 stateless이므로 서버에서 토큰을 무효화할 수 없습니다.
        // 클라이언트에서 토큰을 삭제하도록 가이드합니다.
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success(null,
                "로그아웃 성공. 클라이언트에서 토큰을 삭제해주세요."));
    }
}
