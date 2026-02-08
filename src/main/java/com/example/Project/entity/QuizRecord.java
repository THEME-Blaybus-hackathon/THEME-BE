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
    private String quizId;

    @Column(nullable = false)
    private String objectName;

    @Column
    private String selectedPart;

    @Column
    private String userId;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Integer correctAnswers;

    @Column(nullable = false)
    private Double score;

    @Column
    private String grade;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant submittedAt;

    @Column(length = 5000)
    private String questionsJson; // 퀴즈 문제 리스트(JSON)
}
