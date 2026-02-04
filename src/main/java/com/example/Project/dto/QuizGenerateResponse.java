package com.example.Project.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 퀴즈 생성 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizGenerateResponse {

    private String quizId;  // 퀴즈 세션 ID
    private String objectName;  // 3D 모델 이름
    private String selectedPart;  // 선택된 부품 (있는 경우)
    private List<QuizQuestion> questions;  // 문제 목록 (정답 제외)
    private Instant generatedAt;  // 생성 시각

    /**
     * 정답을 제거한 클라이언트용 문제 목록 생성 (OX 형식)
     */
    public static QuizGenerateResponse fromQuestionsWithoutAnswers(
            String quizId,
            String objectName,
            String selectedPart,
            List<QuizQuestion> questions) {

        // OX 퀴즈: 클라이언트에는 정답과 해설을 보내지 않음
        List<QuizQuestion> clientQuestions = questions.stream()
                .map(q -> QuizQuestion.builder()
                .id(q.getId())
                .question(q.getQuestion())
                .category(q.getCategory())
                // correctAnswer와 explanation 제외 (보안)
                .build())
                .toList();

        return QuizGenerateResponse.builder()
                .quizId(quizId)
                .objectName(objectName)
                .selectedPart(selectedPart)
                .questions(clientQuestions)
                .generatedAt(Instant.now())
                .build();
    }
}
