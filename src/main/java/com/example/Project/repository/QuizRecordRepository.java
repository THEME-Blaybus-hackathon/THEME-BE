package com.example.Project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Project.entity.QuizRecord;

/**
 * 퀴즈 기록 Repository
 */
@Repository
public interface QuizRecordRepository extends JpaRepository<QuizRecord, Long> {

    Optional<QuizRecord> findByQuizId(String quizId);

    List<QuizRecord> findByUserId(String userId);

    List<QuizRecord> findByObjectName(String objectName);

    List<QuizRecord> findByUserIdAndObjectName(String userId, String objectName);
}
