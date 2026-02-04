package com.example.Project.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 퀴즈 기록 Entity
 */
@Entity
@Table(name = "quiz_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String quizId;  // 퀴즈 세션 ID

    @Column(nullable = false)
    private String objectName;  // 3D 모델 이름

    @Column
    private String selectedPart;  // 선택된 부품

    @Column
    private String userId;  // 사용자 ID (선택사항)

    @Column(nullable = false)
    private Integer totalQuestions;  // 전체 문제 수

    @Column(nullable = false)
    private Integer correctAnswers;  // 정답 수

    @Column(nullable = false)
    private Double score;  // 점수 (문제당 10점, 총 30점)

    @Column  // nullable 허용 (등급 산정 제거)
    private String grade;  // 등급 (사용 안 함)

    @Column(nullable = false)
    private Instant createdAt;  // 생성 시각

    @Column
    private Instant submittedAt;  // 제출 시각
}
