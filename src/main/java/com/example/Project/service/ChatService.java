package com.example.Project.service;

import com.example.Project.entity.ChatMessage;
import com.example.Project.entity.ChatSession;
import com.example.Project.entity.User;
import com.example.Project.exception.SessionNotFoundException;
import com.example.Project.exception.UnauthorizedSessionAccessException;
import com.example.Project.repository.ChatMessageRepository;
import com.example.Project.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * ChatService
 * AI 대화 세션 및 메시지 관리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    /**
     * OpenAI API로 전달할 최대 히스토리 개수
     * 토큰 비용 절감을 위해 최근 N개만 전달
     */
    @Value("${ai.chat.history-limit:20}")
    private int historyLimit;

    /**
     * 새로운 세션 생성
     */
    @Transactional
    public ChatSession createSession(User user, String objectName) {
        String sessionId = generateSessionId();
        
        ChatSession session = ChatSession.builder()
                .sessionId(sessionId)
                .user(user)
                .objectName(objectName)
                .build();
        
        ChatSession savedSession = sessionRepository.save(session);
        log.info("Created new session | sessionId: {} | user: {} | object: {}", 
                sessionId, user.getName(), objectName);
        
        return savedSession;
    }

    /**
     * 직관적인 세션 ID 생성
     * 형식: yyyyMMdd-NNN (예: 20260208-001, 20260208-002)
     */
    private String generateSessionId() {
        // 1. 오늘 날짜 접두사
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 2. 오늘 생성된 세션 개수 조회
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        long todayCount = sessionRepository.countByCreatedAtAfter(startOfDay);
        
        // 3. 순번 생성 (3자리, 001부터 시작)
        String sequence = String.format("%03d", todayCount + 1);
        
        // 4. 최종 세션 ID
        return datePrefix + "-" + sequence;
    }

    /**
     * 세션 조회 (sessionId로) + 소유권 검증
     */
    @Transactional(readOnly = true)
    public ChatSession getSessionBySessionId(String sessionId, User user) {
        ChatSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(
                        "Session not found: " + sessionId));
        
        // 소유권 검증: 세션의 소유자와 현재 사용자가 일치하는지 확인
        if (!session.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized session access attempt | sessionId: {} | userId: {} | ownerId: {}",
                    sessionId, user.getId(), session.getUser().getId());
            throw new UnauthorizedSessionAccessException(
                    "You don't have permission to access this session");
        }
        
        return session;
    }

    /**
     * 세션 조회 (검증 없이, 내부용)
     */
    @Transactional(readOnly = true)
    public ChatSession getSessionBySessionId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(
                        "Session not found: " + sessionId));
    }

    /**
     * 세션 존재 여부 확인
     */
    public boolean sessionExists(String sessionId) {
        return sessionRepository.existsBySessionId(sessionId);
    }

    /**
     * 사용자 메시지 저장
     */
    @Transactional
    public ChatMessage saveUserMessage(ChatSession session, String content, String selectedPart) {
        ChatMessage message = ChatMessage.builder()
                .session(session)
                .role("user")
                .content(content)
                .selectedPart(selectedPart)
                .build();
        
        session.updateActivity();
        sessionRepository.save(session);
        
        return messageRepository.save(message);
    }

    /**
     * AI 응답 메시지 저장
     */
    @Transactional
    public ChatMessage saveAssistantMessage(ChatSession session, String content) {
        ChatMessage message = ChatMessage.builder()
                .session(session)
                .role("assistant")
                .content(content)
                .build();
        
        session.updateActivity();
        sessionRepository.save(session);
        
        return messageRepository.save(message);
    }

    /**
     * 세션의 모든 메시지 조회 (대화 히스토리 - 전체)
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getSessionMessages(ChatSession session) {
        return messageRepository.findBySessionOrderByCreatedAtAsc(session);
    }

    /**
     * 세션의 최근 N개 메시지 조회 (OpenAI API 호출용)
     * 토큰 절약을 위해 최근 메시지만 반환
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getRecentSessionMessages(ChatSession session) {
        List<ChatMessage> recentMessages = messageRepository.findRecentMessages(
                session, 
                PageRequest.of(0, historyLimit)
        );
        
        // DESC로 조회했으므로 ASC로 뒤집기
        Collections.reverse(recentMessages);
        
        log.debug("Retrieved {} recent messages (limit: {}) for session: {}", 
                recentMessages.size(), historyLimit, session.getSessionId());
        
        return recentMessages;
    }

    /**
     * 세션 삭제 (소유권 검증 포함)
     */
    @Transactional
    public void deleteSession(String sessionId, User user) {
        ChatSession session = getSessionBySessionId(sessionId, user);
        sessionRepository.delete(session);
        log.info("Deleted session | sessionId: {} | userId: {}", sessionId, user.getId());
    }

    /**
     * 사용자의 모든 세션 조회
     */
    @Transactional(readOnly = true)
    public List<ChatSession> getUserSessions(User user) {
        return sessionRepository.findByUserOrderByLastActivityAtDesc(user);
    }
}
