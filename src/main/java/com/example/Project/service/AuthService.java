package com.example.Project.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Project.dto.OAuthUserInfo;
import com.example.Project.dto.SignupRequest;
import com.example.Project.entity.User;
import com.example.Project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .role(User.Role.USER)
                .build();

        return userRepository.save(user);
    }

    /**
     * OAuth 사용자 처리 (기존 사용자 확인만, 자동 가입 안 함)
     *
     * @return User 객체 (기존 사용자) 또는 null (신규 사용자)
     */
    @Transactional(readOnly = true)
    public User findOAuthUser(OAuthUserInfo oAuthUserInfo) {
        return userRepository.findByProviderAndProviderId(
                oAuthUserInfo.getProvider(),
                oAuthUserInfo.getProviderId()
        ).orElse(null);
    }

    /**
     * OAuth 신규 사용자 회원가입
     */
    @Transactional
    public User signupOAuthUser(OAuthUserInfo oAuthUserInfo, String phoneNumber) {
        String email = oAuthUserInfo.getEmail();

        // 중복 확인
        if (userRepository.findByProviderAndProviderId(
                oAuthUserInfo.getProvider(),
                oAuthUserInfo.getProviderId()).isPresent()) {
            throw new RuntimeException("이미 가입된 사용자입니다.");
        }

        // 신규 사용자 생성
        User newUser = User.builder()
                .email(email != null ? email : oAuthUserInfo.getProvider() + "_" + oAuthUserInfo.getProviderId() + "@oauth.local")
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 랜덤 비밀번호
                .name(oAuthUserInfo.getName())
                .phoneNumber(phoneNumber)
                .provider(oAuthUserInfo.getProvider())
                .providerId(oAuthUserInfo.getProviderId())
                .role(User.Role.USER)
                .build();

        return userRepository.save(newUser);
    }

    /**
     * 기존 로직 유지 (하위 호환성)
     */
    @Transactional
    public User processOAuthUser(OAuthUserInfo oAuthUserInfo) {
        return userRepository.findByProviderAndProviderId(
                oAuthUserInfo.getProvider(),
                oAuthUserInfo.getProviderId()
        ).orElseGet(() -> {
            User newUser = User.builder()
                    .email(oAuthUserInfo.getEmail() != null ? oAuthUserInfo.getEmail()
                            : oAuthUserInfo.getProvider() + "_" + oAuthUserInfo.getProviderId() + "@oauth.local")
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .name(oAuthUserInfo.getName())
                    .provider(oAuthUserInfo.getProvider())
                    .providerId(oAuthUserInfo.getProviderId())
                    .role(User.Role.USER)
                    .build();

            return userRepository.save(newUser);
        });
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}
