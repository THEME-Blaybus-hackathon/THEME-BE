package com.example.Project.repository;

import com.example.Project.entity.ChatSession;
import com.example.Project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    /**
     * sessionId로 세션 조회
     */
    Optional<ChatSession> findBySessionId(String sessionId);

    /**
     * 사용자의 모든 세션 조회
     */
    List<ChatSession> findByUserOrderByLastActivityAtDesc(User user);

    /**
     * 사용자의 특정 객체에 대한 세션 조회
     */
    List<ChatSession> findByUserAndObjectNameOrderByLastActivityAtDesc(User user, String objectName);

    /**
     * sessionId 존재 여부 확인
     */
    boolean existsBySessionId(String sessionId);

    /**
     * 특정 날짜 이후 생성된 세션 개수 조회 (세션 ID 생성용)
     */
    @Query("SELECT COUNT(cs) FROM ChatSession cs WHERE cs.createdAt >= :startOfDay")
    long countByCreatedAtAfter(@Param("startOfDay") LocalDateTime startOfDay);

    /**
     * 오늘 날짜로 시작하는 sessionId 중 가장 큰 순번 추출 (세션ID 중복 방지)
     */
    @Query("SELECT MAX(CAST(SUBSTRING(cs.sessionId, 10, 3) AS int)) FROM ChatSession cs WHERE cs.sessionId LIKE CONCAT(:datePrefix, '-%')")
    Integer findMaxSequenceByDatePrefix(@Param("datePrefix") String datePrefix);
}
