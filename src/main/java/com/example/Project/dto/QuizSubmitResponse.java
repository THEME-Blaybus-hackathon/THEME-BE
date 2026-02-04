package com.example.Project.dto;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 퀴즈 제출 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmitResponse {

    private String quizId;  // 퀴즈 세션 ID
    private Integer totalQuestions;  // 전체 문제 수
    private Integer correctAnswers;  // 정답 수
    private Integer wrongAnswers;  // 오답 수
    private Double score;  // 점수 (0-100)
    private String grade;  // 등급 (A+, A, B+, B, C, F)
    private Map<String, QuizResult> results;  // 문제별 결과
    private Instant submittedAt;  // 제출 시각

    /**
     * 개별 문제 결과 (OX 형식)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizResult {

        private String questionId;
        private Boolean isCorrect;  // 정답 여부
        private String userAnswer;  // 사용자 답변 ("O" 또는 "X")
        private String correctAnswer;  // 정답 ("O" 또는 "X")
        private String explanation;  // 해설
        private String question;  // 문제 내용
    }

    /**
     * 점수에 따른 등급 계산
     */
    public static String calculateGrade(double score) {
        if (score >= 95) {
            return "A+";
        }
        if (score >= 90) {
            return "A";
        }
        if (score >= 85) {
            return "B+";
        }
        if (score >= 80) {
            return "B";
        }
        if (score >= 70) {
            return "C";
        }
        return "F";
    }
}
