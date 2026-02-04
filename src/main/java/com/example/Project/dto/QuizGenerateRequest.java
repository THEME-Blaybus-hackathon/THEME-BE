package com.example.Project.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 퀴즈 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizGenerateRequest {

    @NotBlank(message = "Object name is required")
    private String objectName;  // v4_engine, drone 등

    private String selectedPart;  // 특정 부품에 대한 퀴즈 (선택사항)

    @Min(value = 1, message = "Question count must be at least 1")
    @Max(value = 10, message = "Question count must not exceed 10")
    @Builder.Default
    private Integer questionCount = 5;  // 기본 5문제

    private String difficulty;  // "easy", "medium", "hard" (선택사항)
}
