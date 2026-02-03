package com.example.Project.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.ApiResponse;
import com.example.Project.dto.UserProgressResponse;
import com.example.Project.dto.UserResponse;
import com.example.Project.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "사용자 API", description = "사용자 정보 및 학습 진도 관리 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "내 정보 조회",
            description = "로그인한 사용자의 프로필 정보를 조회합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
        String email = getCurrentUserEmail();
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user, "사용자 정보 조회 성공"));
    }

    @Operation(
            summary = "학습 진도 조회",
            description = "로그인한 사용자가 학습한 3D 모델 목록과 진도를 조회합니다. 최근 접근 순으로 정렬됩니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<List<UserProgressResponse>>> getProgress() {
        String email = getCurrentUserEmail();
        List<UserProgressResponse> progress = userService.getUserProgress(email);
        return ResponseEntity.ok(ApiResponse.success(progress, "학습 진도 조회 성공"));
    }

    @Operation(
            summary = "학습 진도 기록",
            description = "사용자가 3D 모델을 열거나 부품을 클릭할 때 학습 진도를 기록합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @PostMapping("/progress")
    public ResponseEntity<ApiResponse<Void>> recordProgress(
            @Parameter(description = "3D 모델명 (예: v4_engine, suspension)", required = true)
            @RequestParam String objectName,
            @Parameter(description = "마지막 본 부품명 (선택)")
            @RequestParam(required = false) String lastViewedPart) {

        String email = getCurrentUserEmail();
        userService.recordProgress(email, objectName, lastViewedPart);
        return ResponseEntity.ok(ApiResponse.success(null, "학습 진도 기록 성공"));
    }

    /**
     * 현재 로그인한 사용자의 이메일을 가져옵니다
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다");
        }
        return authentication.getName();
    }
}
