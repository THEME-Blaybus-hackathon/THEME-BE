package com.example.Project.controller;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Project.dto.OAuthUserInfo;
import com.example.Project.dto.SignupRequest;
import com.example.Project.entity.User;
import com.example.Project.service.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupRequest signupRequest,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        try {
            authService.signup(signupRequest);
            return "redirect:/login?signup=success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication != null) {
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            model.addAttribute("user", user);
        }
        return "dashboard";
    }

    @GetMapping("/oauth-signup")
    public String oauthSignupPage(HttpSession session, Model model) {
        OAuthUserInfo pendingUser = (OAuthUserInfo) session.getAttribute("pendingOAuthUser");

        if (pendingUser == null) {
            return "redirect:/login?error=session";
        }

        model.addAttribute("oauthUser", pendingUser);
        return "oauth-signup";
    }

    @PostMapping("/oauth-signup")
    public String completeOAuthSignup(@RequestParam(required = false) String phoneNumber,
            HttpSession session) {
        OAuthUserInfo pendingUser = (OAuthUserInfo) session.getAttribute("pendingOAuthUser");

        if (pendingUser == null) {
            return "redirect:/login?error=session";
        }

        try {
            // OAuth 사용자 회원가입
            User user = authService.signupOAuthUser(pendingUser, phoneNumber);

            // 세션에서 임시 정보 제거
            session.removeAttribute("pendingOAuthUser");

            // 자동 로그인 처리
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            return "redirect:/oauth-signup?error=" + e.getMessage();
        }
    }
}
