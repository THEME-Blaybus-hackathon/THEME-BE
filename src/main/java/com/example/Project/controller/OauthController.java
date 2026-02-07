package com.example.Project.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.OAuthUserInfo;
import com.example.Project.entity.User;
import com.example.Project.helper.constants.SocialLoginType;
import com.example.Project.security.JwtTokenProvider;
import com.example.Project.service.AuthService;
import com.example.Project.service.OauthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "OAuth Social Login", description = "소셜 로그인 API (Google, Kakao, Naver)")
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Slf4j
public class OauthController {

    private static final String OAUTH_TYPE_SESSION_KEY = "oauth_type";
    private static final String OAUTH_TYPE_API = "api";

    private final OauthService oauthService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "OAuth 로그인 시작",
            description = "소셜 로그인 제공자의 인증 페이지로 리다이렉트합니다. (Google, Kakao, Naver)"
    )
    @GetMapping(value = "/{socialLoginType}")
    public void socialLoginType(
            @Parameter(description = "소셜 로그인 타입", example = "google")
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
            @Parameter(description = "요청 타입 (web: 세션, api: JWT)", example = "web")
            @RequestParam(name = "type", required = false, defaultValue = "web") String type,
            HttpServletResponse response,
            HttpSession session) throws IOException {

        log.info("🔐 OAuth login initiated: {} (type: {})", socialLoginType, type);

        // API 요청 타입 세션에 저장
        if (OAUTH_TYPE_API.equalsIgnoreCase(type)) {
            session.setAttribute(OAUTH_TYPE_SESSION_KEY, OAUTH_TYPE_API);
        }

        String redirectUrl = oauthService.getRedirectUrl(socialLoginType);
        log.info("↗️  Redirecting to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    @Operation(
            summary = "OAuth 콜백",
            description = "소셜 로그인 제공자로부터 인증 코드를 받아 처리합니다."
    )
    @GetMapping(value = "/{socialLoginType}/callback")
    public void callback(
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
            @RequestParam(name = "code") String code,
            HttpServletResponse response,
            HttpSession session) throws IOException {

        log.info("🔑 OAuth callback received from {}", socialLoginType);

        // 1. Authorization Code → Access Token
        String accessToken = oauthService.requestAccessToken(socialLoginType, code);
        if (accessToken == null) {
            log.error("❌ Failed to get access token");
            handleOAuthError(response, session, "액세스 토큰 획득 실패");
            return;
        }
        log.debug("✅ Access token obtained");

        // 2. Access Token → User Info
        OAuthUserInfo userInfo = oauthService.getUserInfo(socialLoginType, accessToken);
        if (userInfo == null) {
            log.error("❌ Failed to get user info");
            handleOAuthError(response, session, "사용자 정보 획득 실패");
            return;
        }
        log.info("✅ User info obtained: {}", userInfo.getEmail());

        // 3. 기존 사용자 확인
        User user = authService.findOAuthUser(userInfo);
        boolean isApiRequest = isApiRequest(session);

        // 4-1. 신규 사용자
        if (user == null) {
            handleNewUser(userInfo, isApiRequest, response, session);
            return;
        }

        // 4-2. 기존 사용자
        handleExistingUser(user, isApiRequest, response, session);
    }

    /**
     * 신규 사용자 처리
     */
    private void handleNewUser(OAuthUserInfo userInfo, boolean isApiRequest,
            HttpServletResponse response, HttpSession session) throws IOException {
        log.info("👤 New user detected: {}", userInfo.getEmail());

        // 자동 회원가입 + JWT 반환 (프론트엔드로 리다이렉트)
        log.info("🔧 Auto signup and redirect to frontend");
        User newUser = authService.signupOAuthUser(userInfo, userInfo.getEmail());
        respondWithJwt(response, session, newUser, true);
    }

    /**
     * 기존 사용자 처리
     */
    private void handleExistingUser(User user, boolean isApiRequest,
            HttpServletResponse response, HttpSession session) throws IOException {
        log.info("✅ Existing user login: {}", user.getEmail());

        // 모든 OAuth 로그인은 프론트엔드로 리다이렉트 (JWT 방식 통일)
        respondWithJwt(response, session, user, false);
    }

    /**
     * API 요청 여부 확인
     */
    private boolean isApiRequest(HttpSession session) {
        String oauthType = (String) session.getAttribute(OAUTH_TYPE_SESSION_KEY);
        return OAUTH_TYPE_API.equalsIgnoreCase(oauthType);
    }

    /**
     * JWT 토큰 응답 (프론트엔드로 리다이렉트)
     */
    private void respondWithJwt(HttpServletResponse response, HttpSession session,
            User user, boolean isNewUser) throws IOException {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        session.removeAttribute(OAUTH_TYPE_SESSION_KEY);

        // 프론트엔드로 리다이렉트 (Query String으로 토큰 전달)
        String frontendUrl = "http://localhost:5173/auth/callback"
            + "?accessToken=" + accessToken
            + "&refreshToken=" + refreshToken
            + "&tokenType=Bearer"
            + "&isNewUser=" + isNewUser;

        response.sendRedirect(frontendUrl);

        log.info("✅ JWT tokens issued, redirecting to frontend (isNewUser: {})", isNewUser);
    }

    /**
     * OAuth 에러 처리
     */
    private void handleOAuthError(HttpServletResponse response, HttpSession session,
            String errorMessage) throws IOException {
        log.error("❌ OAuth error: {}", errorMessage);

        boolean isApiRequest = isApiRequest(session);
        session.removeAttribute(OAUTH_TYPE_SESSION_KEY);

        if (isApiRequest) {
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(String.format(
                    "{\"success\":false,\"message\":\"%s\",\"data\":null}",
                    errorMessage
            ));
        } else {
            response.sendRedirect("/login?error=oauth");
        }
    }
}
