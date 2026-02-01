package com.example.Project.controller;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Slf4j
public class OauthController {

    private final OauthService oauthService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping(value = "/{socialLoginType}")
    public void socialLoginType(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
            @RequestParam(name = "type", required = false, defaultValue = "web") String type,
            HttpServletResponse response,
            HttpSession session) throws IOException {
        log.info("SNS login request received :: {} (type: {})", socialLoginType, type);

        if ("api".equalsIgnoreCase(type)) {
            session.setAttribute("oauth_type", "api");
        }

        String redirectUrl = oauthService.getRedirectUrl(socialLoginType);
        log.info("Redirect URL :: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping(value = "/{socialLoginType}/callback")
    public void callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
            @RequestParam(name = "code") String code,
            HttpServletResponse response,
            HttpSession session) throws IOException {
        log.info("Received authorization code from {} :: {}", socialLoginType, code);

        String accessToken = oauthService.requestAccessToken(socialLoginType, code);
        log.info("Access Token :: {}", accessToken);

        if (accessToken == null) {
            handleOAuthError(response, session, "액세스 토큰 획득 실패");
            return;
        }

        OAuthUserInfo userInfo = oauthService.getUserInfo(socialLoginType, accessToken);
        log.info("OAuth User Info :: {}", userInfo);

        if (userInfo == null) {
            handleOAuthError(response, session, "사용자 정보 획득 실패");
            return;
        }

        User user = authService.findOAuthUser(userInfo);

        String oauthType = (String) session.getAttribute("oauth_type");
        boolean isApiRequest = "api".equalsIgnoreCase(oauthType);

        if (user == null) {
            log.info("New user detected");

            if (isApiRequest) {
                log.info("API request - auto signup");
                user = authService.signupOAuthUser(userInfo, userInfo.getEmail());
                redirectToApiSuccess(response, user, true);
            } else {
                log.info("Web request - redirect to additional info page");
                session.setAttribute("pendingOAuthUser", userInfo);
                response.sendRedirect("/oauth-signup");
            }
            return;
        }

        log.info("Existing user login :: {}", user.getEmail());

        if (isApiRequest) {
            redirectToApiSuccess(response, user, false);
        } else {
            createSessionAndRedirect(user, session, response);
        }
    }

    private void createSessionAndRedirect(User user, HttpSession session, HttpServletResponse response) throws IOException {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        session.removeAttribute("oauth_type");

        response.sendRedirect("/dashboard");
    }

    private void redirectToApiSuccess(HttpServletResponse response, User user, boolean isNewUser) throws IOException {
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
                "{\"success\":true,\"message\":\"로그인 성공\",\"data\":{\"accessToken\":\"%s\",\"refreshToken\":\"%s\",\"tokenType\":\"Bearer\",\"isNewUser\":%b}}",
                accessToken, refreshToken, isNewUser
        ));
    }

    private void handleOAuthError(HttpServletResponse response, HttpSession session, String errorMessage) throws IOException {
        log.error("OAuth error: {}", errorMessage);

        String oauthType = (String) session.getAttribute("oauth_type");
        boolean isApiRequest = "api".equalsIgnoreCase(oauthType);

        if (isApiRequest) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(String.format(
                    "{\"success\":false,\"message\":\"%s\",\"data\":null}",
                    errorMessage
            ));
        } else {
            response.sendRedirect("/login?error=oauth");
        }

        session.removeAttribute("oauth_type");
    }
}
