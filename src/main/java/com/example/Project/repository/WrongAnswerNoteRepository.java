package com.example.Project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Project.entity.WrongAnswerNote;

/**
 * 오답 노트 리포지토리
 */
@Repository
public interface WrongAnswerNoteRepository extends JpaRepository<WrongAnswerNote, Long> {

    /**
     * 특정 사용자의 모든 오답 노트 조회
     */
    List<WrongAnswerNote> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * 특정 사용자의 특정 객체에 대한 오답 노트 조회
     */
    List<WrongAnswerNote> findByUserIdAndObjectNameOrderByCreatedAtDesc(String userId, String objectName);

    /**
     * 특정 사용자의 미복습 오답 노트 조회
     */
    List<WrongAnswerNote> findByUserIdAndReviewedFalseOrderByCreatedAtDesc(String userId);

    /**
     * 특정 사용자의 특정 퀴즈에 대한 오답 노트 조회
     */
    List<WrongAnswerNote> findByUserIdAndQuizId(String userId, String quizId);
}
