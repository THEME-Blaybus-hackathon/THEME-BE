package com.example.Project.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 퀴즈 제출 요청 DTO (OX 형식)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmitRequest {

    @Schema(description = "퀴즈 세션 ID", example = "8d36add3-b941-4f5d-96bd-9ff3bf249288")
    @NotBlank(message = "Quiz ID is required")
    private String quizId;  // 퀴즈 세션 ID

    @Schema(description = "문제ID별 답변 (O/X)", example = "{\"q1\": \"O\", \"q2\": \"X\", \"q3\": \"O\"}")
    @NotNull(message = "Answers are required")
    private Map<String, String> answers;  // 문제ID -> 선택한 답변 ("O" 또는 "X")
}
