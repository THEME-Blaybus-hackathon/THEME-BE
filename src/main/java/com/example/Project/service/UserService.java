package com.example.Project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Project.dto.SignupRequest;
import com.example.Project.dto.UserProgressResponse;
import com.example.Project.dto.UserResponse;
import com.example.Project.entity.User;
import com.example.Project.entity.UserProgress;
import com.example.Project.repository.UserProgressRepository;
import com.example.Project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일로 사용자 조회
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
        return UserResponse.from(user);
    }

    /**
     * 회원가입
     */
    @Transactional
    public UserResponse signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .role(User.Role.USER)
                .provider("LOCAL")
                .build();

        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    /**
     * 사용자의 학습 진도 조회 (최근 접근 순)
     */
    public List<UserProgressResponse> getUserProgress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));

        List<UserProgress> progressList = userProgressRepository.findByUserOrderByLastAccessedAtDesc(user);

        return progressList.stream()
                .map(UserProgressResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 학습 진도 기록 (3D 모델 접근 시)
     */
    @Transactional
    public void recordProgress(String email, String objectName, String lastViewedPart) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));

        // 기존 진도가 있으면 업데이트, 없으면 생성
        UserProgress progress = userProgressRepository.findByUserAndObjectName(user, objectName)
                .orElse(UserProgress.builder()
                        .user(user)
                        .objectName(objectName)
                        .visitCount(0)
                        .build());

        // 방문 횟수 증가
        progress.setVisitCount(progress.getVisitCount() + 1);

        // 마지막 본 부품 업데이트
        if (lastViewedPart != null && !lastViewedPart.isEmpty()) {
            progress.setLastViewedPart(lastViewedPart);
        }

        userProgressRepository.save(progress);
    }
}
