package com.example.Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 퀴즈 문제 DTO (OX 형식)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {

    private String id;  // 문제 고유 ID
    private String question;  // 문제 내용 (서술형 문장)
    private String correctAnswer;  // 정답 ("O" 또는 "X")
    private String explanation;  // 정답 해설
    private String category;  // 카테고리 (예: "구조", "기능", "재질")
}
