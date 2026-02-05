package com.example.Project.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;   // PDF 매칭용 (필수)
    private String objectName;  // 부품 매칭용 (필수)
    
    private String question;       // 문제 내용
    private String userAnswer;     // 내가 고른 답 (O/X)
    private String correctAnswer;  // 진짜 정답 (O/X)
    private boolean isCorrect;     // 정답 여부
    
    // 설명이 길 수 있으므로 길이 제한을 넉넉하게
    @jakarta.persistence.Column(length = 1000)
    private String explanation;    // 해설

    @CreatedDate
    private LocalDateTime createdAt;
}