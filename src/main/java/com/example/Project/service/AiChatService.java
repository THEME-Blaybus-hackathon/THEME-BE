package com.example.Project.service;

import com.example.Project.dto.AiChatRequest;
import com.example.Project.dto.AiChatResponse;
import com.example.Project.dto.ChatHistoryResponse;
import com.example.Project.entity.ChatMessage;
import com.example.Project.entity.ChatSession;
import com.example.Project.entity.User;
import com.example.Project.exception.ChatProcessingException;
import com.example.Project.exception.SessionNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * AiChatService
 * 새로운 AI Chat API 로직 (DB 기반 세션 관리)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

    private final ChatService chatService;
    private final OpenAiService openAiService;
    private final PromptService promptService;

    /**
     * AI 채팅 처리 (개선된 버전)
     * 
     * ✅ 개선 사항:
     * 1. 트랜잭션 처리: OpenAI 실패 시 전체 롤백
     * 2. 세션 소유권 검증: 다른 사용자의 세션 접근 차단
     * 3. 히스토리 제한: 최근 N개만 OpenAI에 전달 (토큰 절약)
     */
    @Transactional
    public AiChatResponse processChat(User user, AiChatRequest request) {
        log.info("Processing AI chat | user: {} | sessionId: {} | object: {} | message: {}",
                user.getName(), request.getSessionId(), request.getObjectName(), 
                request.getMessage().substring(0, Math.min(50, request.getMessage().length())));

        try {
            // 1. 세션 조회 또는 생성
            ChatSession session;
            boolean isNewSession = false;
            String objectName;
            
            if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
                // 새 세션 생성 시 objectName 필수
                if (request.getObjectName() == null || request.getObjectName().isEmpty()) {
                    throw new IllegalArgumentException("objectName is required for new session");
                }
                
                // 객체 이름 검증
                if (!promptService.isValidObjectId(request.getObjectName())) {
                    throw new IllegalArgumentException("Invalid object name: " + request.getObjectName());
                }
                
                session = chatService.createSession(user, request.getObjectName());
                objectName = request.getObjectName();
                isNewSession = true;
                log.info("Created new session | sessionId: {}", session.getSessionId());
            } else {
                // 기존 세션 조회 + 소유권 검증
                session = chatService.getSessionBySessionId(request.getSessionId(), user);
                objectName = session.getObjectName(); // 세션에서 objectName 가져오기
                log.info("Using existing session | sessionId: {} | object: {}", 
                        session.getSessionId(), objectName);
            }

            // 2. 시스템 프롬프트 가져오기
            String systemPrompt = promptService.getSystemPrompt(objectName);

            // 3. 대화 히스토리 조회 (최근 N개만 - 토큰 절약)
            List<ChatMessage> recentMessages = chatService.getRecentSessionMessages(session);
            log.info("Loaded {} recent messages for OpenAI context", recentMessages.size());
            
            // 4. OpenAI API 호출용 메시지 구성
            List<com.example.Project.dto.ChatMessage> openAiMessages = buildOpenAiMessages(
                    systemPrompt, 
                    recentMessages, 
                    request.getMessage(), 
                    request.getSelectedPart()
            );

            // 5. 사용자 질문 저장 (OpenAI 호출 전)
            ChatMessage userMessage = chatService.saveUserMessage(
                    session, 
                    request.getMessage(), 
                    request.getSelectedPart()
            );

            // 6. OpenAI API 호출
            String aiAnswer = openAiService.sendChatCompletion(openAiMessages);
            log.info("Received AI answer | length: {}", aiAnswer.length());

            // 7. AI 답변 저장
            ChatMessage assistantMessage = chatService.saveAssistantMessage(session, aiAnswer);

            // 8. 응답 생성
            return AiChatResponse.of(
                    session.getSessionId(),
                    aiAnswer,
                    isNewSession,
                    assistantMessage.getId()
            );
            
        } catch (SessionNotFoundException | IllegalArgumentException e) {
            // 비즈니스 로직 예외는 그대로 throw
            throw e;
        } catch (Exception e) {
            // OpenAI 호출 실패 등 예상치 못한 오류
            log.error("Chat processing failed | user: {} | error: {}", 
                    user.getName(), e.getMessage(), e);
            throw new ChatProcessingException("Failed to process chat request", e);
        }
    }

    /**
     * 대화 히스토리 조회 (소유권 검증 포함)
     */
    @Transactional(readOnly = true)
    public ChatHistoryResponse getChatHistory(String sessionId, User user) {
        ChatSession session = chatService.getSessionBySessionId(sessionId, user);
        List<ChatMessage> messages = chatService.getSessionMessages(session);
        
        return ChatHistoryResponse.from(
                session.getSessionId(),
                session.getObjectName(),
                messages
        );
    }

    /**
     * 세션 삭제 (소유권 검증 포함)
     */
    @Transactional
    public void deleteSession(String sessionId, User user) {
        chatService.deleteSession(sessionId, user);
    }

    /**
     * OpenAI API 호출용 메시지 리스트 구성
     * DB의 ChatMessage 엔티티를 OpenAI DTO로 변환
     */
    private List<com.example.Project.dto.ChatMessage> buildOpenAiMessages(
            String systemPrompt,
            List<ChatMessage> dbMessages,
            String newQuestion,
            String selectedPart
    ) {
        List<com.example.Project.dto.ChatMessage> messages = new ArrayList<>();

        // 1. 시스템 프롬프트
        messages.add(com.example.Project.dto.ChatMessage.builder()
                .role("system")
                .content(systemPrompt)
                .build());

        // 2. 이전 대화 히스토리 추가
        for (ChatMessage dbMsg : dbMessages) {
            String content = dbMsg.getContent();
            
            // user 메시지에 selectedPart 정보 포함
            if ("user".equals(dbMsg.getRole()) && dbMsg.getSelectedPart() != null) {
                content = String.format("[선택된 부품: %s]\n%s", 
                        dbMsg.getSelectedPart(), content);
            }
            
            messages.add(com.example.Project.dto.ChatMessage.builder()
                    .role(dbMsg.getRole())
                    .content(content)
                    .build());
        }

        // 3. 새 사용자 질문 추가
        String userContent = newQuestion;
        if (selectedPart != null && !selectedPart.isEmpty()) {
            userContent = String.format("[선택된 부품: %s]\n%s", selectedPart, newQuestion);
        }
        
        messages.add(com.example.Project.dto.ChatMessage.builder()
                .role("user")
                .content(userContent)
                .build());

        log.debug("Built {} messages for OpenAI (system + {} history + 1 new)", 
                messages.size(), dbMessages.size());

        return messages;
    }
}
