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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 오답 노트 엔티티
 * 사용자가 틀린 퀴즈 문제를 저장하여 복습 가능
 */
@Entity
@Table(name = "wrong_answer_notes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongAnswerNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;  // 사용자 ID

    @Column(nullable = false)
    private String quizId;  // 퀴즈 세션 ID

    @Column(nullable = false)
    private String questionId;  // 문제 ID (q1, q2, ...)

    @Column(nullable = false, length = 1000)
    private String question;  // 문제 내용

    @Column(nullable = false)
    private String userAnswer;  // 사용자 답변 ("O" 또는 "X")

    @Column(nullable = false)
    private String correctAnswer;  // 정답 ("O" 또는 "X")

    @Column(nullable = false, length = 2000)
    private String explanation;  // 정답 해설

    @Column
    private String category;  // 카테고리

    @Column(nullable = false)
    private String objectName;  // 3D 모델 이름 (v4_engine, drone, ...)

    @Column(nullable = false)
    private Instant createdAt;  // 오답 저장 시각

    @Column
    private Boolean reviewed;  // 복습 완료 여부

    @Column
    private Instant reviewedAt;  // 복습 완료 시각
}
