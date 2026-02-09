package com.example.Project.repository;

import com.example.Project.entity.ChatMessage;
import com.example.Project.entity.ChatSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 특정 세션의 모든 메시지 조회 (시간순)
     */
    List<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session);

    /**
     * 특정 세션의 최근 N개 메시지 조회 (히스토리 제한)
     * OpenAI API 토큰 절약을 위해 사용
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.session = :session " +
           "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findRecentMessages(@Param("session") ChatSession session, Pageable pageable);

    /**
     * 특정 세션의 메시지 개수
     */
    long countBySession(ChatSession session);

    /**
     * 특정 세션의 모든 메시지 삭제
     */
    void deleteBySession(ChatSession session);

    /**
     * sessionId로 메시지 전체 조회 (시간순)
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.session.id = :sessionId ORDER BY m.createdAt ASC")
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(@Param("sessionId") Long sessionId);
}
