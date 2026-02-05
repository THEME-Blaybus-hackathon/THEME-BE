package com.example.Project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project.entity.QuizAnswer;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    
    // 특정 세션에서 특정 부품에 대해 풀었던 모든 문제 조회 (최신순)
    List<QuizAnswer> findBySessionIdAndObjectNameOrderByCreatedAtDesc(String sessionId, String objectName);
}