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
    private String userId;

    @Column(nullable = false)
    private String quizId;

    @Column(nullable = false)
    private String questionId;

    @Column(nullable = false, length = 1000)
    private String question;

    @Column(nullable = false)
    private String userAnswer;

    @Column(nullable = false)
    private String correctAnswer;

    @Column(nullable = false, length = 2000)
    private String explanation;

    @Column
    private String category;

    @Column(nullable = false)
    private String objectName;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Boolean reviewed;

    @Column
    private Instant reviewedAt;
}
