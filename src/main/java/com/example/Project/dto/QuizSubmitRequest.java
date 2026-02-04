package com.example.Project.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 퀴즈 제출 요청 DTO (OX 형식)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmitRequest {

    @NotBlank(message = "Quiz ID is required")
    private String quizId;  // 퀴즈 세션 ID

    @NotNull(message = "Answers are required")
    private Map<String, String> answers;  // 문제ID -> 선택한 답변 ("O" 또는 "X")

    private String userId;  // 사용자 ID (선택사항, 로그인 시)
}
