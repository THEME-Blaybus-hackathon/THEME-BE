package com.example.Project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Project.dto.AiChatRequest;
import com.example.Project.dto.AiChatResponse;
import com.example.Project.dto.ChatHistoryResponse;
import com.example.Project.entity.ChatMessage;
import com.example.Project.entity.ChatSession;
import com.example.Project.entity.User;
import com.example.Project.exception.ChatProcessingException;
import com.example.Project.exception.SessionNotFoundException;
import com.example.Project.repository.ChatMessageRepository;
import com.example.Project.repository.ChatSessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    private static final String RESPONSE_STYLE_GUIDE = """
            
          [응답 형식 강제 규칙]
            
            - Markdown 형식으로 작성할 것.
            - 모든 내용은 "-" 불렛포인트만 사용할 것.
            - 답변은 반드시 아래 구조를 따를 것:
            
              1) 한 줄 요약
              2) 핵심 기능
              3) 구조 및 작동 원리
              4) 목적 및 역할
              5) 유지관리 및 주의사항
            
            - 각 섹션 제목은 **볼드체 소제목**으로 작성할 것.
            - 모든 문장은 명사형 종결어미로 끝낼 것.
            - 핵심 키워드는 반드시 **볼드체**로 강조할 것.
            - 한 문장은 2줄을 넘기지 말 것.
            - 서론, 결론, 인사말을 포함하지 말 것.
            - 중복 표현을 금지할 것.
            - 형식을 어길 경우 스스로 수정하여 재작성할 것.
            
            이 답변은 자동 문서화 시스템에 직접 삽입됨.
            형식을 반드시 준수할 것.
            
            위 규칙을 내부 시스템 지침으로 유지한 채 아래 질문에 답할 것.
            """;

    @Transactional
    public AiChatResponse processChat(User user, AiChatRequest request) {
        log.info("Processing AI chat | user: {} | sessionId: {} | object: {} | message: {}",
                user.getName(), request.getSessionId(), request.getObjectName(), 
                request.getMessage().substring(0, Math.min(50, request.getMessage().length())));

        try {
            ChatSession session;
            boolean isNewSession = false;
            String objectName;
            
            if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
                if (request.getObjectName() == null || request.getObjectName().isEmpty()) {
                    throw new IllegalArgumentException("objectName is required for new session");
                }
                
                if (!promptService.isValidObjectId(request.getObjectName())) {
                    throw new IllegalArgumentException("Invalid object name: " + request.getObjectName());
                }
                
                session = chatService.createSession(user, request.getObjectName());
                objectName = request.getObjectName();
                isNewSession = true;
                log.info("Created new session | sessionId: {}", session.getSessionId());
            } else {
                session = chatService.getSessionBySessionId(request.getSessionId(), user);
                objectName = session.getObjectName();
                log.info("Using existing session | sessionId: {} | object: {}", 
                        session.getSessionId(), objectName);
            }

            String systemPrompt = promptService.getSystemPrompt(objectName);

            List<ChatMessage> recentMessages = chatService.getRecentSessionMessages(session);
            
            List<com.example.Project.dto.ChatMessage> openAiMessages = buildOpenAiMessages(
                    systemPrompt, 
                    recentMessages, 
                    request.getMessage(), 
                    request.getSelectedPart()
            );

            ChatMessage userMessage = chatService.saveUserMessage(
                    session, 
                    request.getMessage(), 
                    request.getSelectedPart()
            );

            String aiAnswer = openAiService.sendChatCompletion(openAiMessages);
            log.info("Received AI answer | length: {}", aiAnswer.length());

            ChatMessage assistantMessage = chatService.saveAssistantMessage(session, aiAnswer);

            return AiChatResponse.of(
                    session.getSessionId(),
                    aiAnswer,
                    isNewSession,
                    assistantMessage.getId()
            );
            
        } catch (SessionNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Chat processing failed | user: {} | error: {}", 
                    user.getName(), e.getMessage(), e);
            throw new ChatProcessingException("Failed to process chat request", e);
        }
    }

    // ... (getChatHistory, deleteSession, getAllChatHistoryByObjectForUser 메서드는 기존과 동일하므로 생략) ...
    @Transactional(readOnly = true)
    public ChatHistoryResponse getChatHistory(String sessionId, User user) {
        ChatSession session = chatService.getSessionBySessionId(sessionId, user);
        List<ChatMessage> messages = chatService.getSessionMessages(session);
        return ChatHistoryResponse.from(session.getSessionId(), session.getObjectName(), messages);
    }

    @Transactional
    public void deleteSession(String sessionId, User user) {
        chatService.deleteSession(sessionId, user);
    }

    @Transactional(readOnly = true)
    public List<ChatHistoryResponse> getAllChatHistoryByObjectForUser(Long userId, String objectName) {
        List<ChatSession> sessions = chatSessionRepository.findByUserIdAndObjectNameOrderByCreatedAtDesc(userId, objectName);
        List<ChatHistoryResponse> result = new ArrayList<>();
        for (ChatSession session : sessions) {
            List<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());
            result.add(ChatHistoryResponse.from(session.getSessionId(), session.getObjectName(), messages));
        }
        return result;
    }

    private List<com.example.Project.dto.ChatMessage> buildOpenAiMessages(
            String systemPrompt,
            List<ChatMessage> dbMessages,
            String newQuestion,
            String selectedPart
    ) {
        List<com.example.Project.dto.ChatMessage> messages = new ArrayList<>();

        String finalSystemPrompt = systemPrompt + "\n" + RESPONSE_STYLE_GUIDE;

        messages.add(com.example.Project.dto.ChatMessage.builder()
                .role("system")
                .content(finalSystemPrompt)
                .build());

        for (ChatMessage dbMsg : dbMessages) {
            String content = dbMsg.getContent();
            
            if ("user".equals(dbMsg.getRole()) && dbMsg.getSelectedPart() != null) {
                content = String.format("[선택된 부품: %s]\n%s", 
                        dbMsg.getSelectedPart(), content);
            }
            
            messages.add(com.example.Project.dto.ChatMessage.builder()
                    .role(dbMsg.getRole())
                    .content(content)
                    .build());
        }

        String userContent = newQuestion;
        if (selectedPart != null && !selectedPart.isEmpty()) {
            userContent = String.format("[선택된 부품: %s]\n%s", selectedPart, newQuestion);
        }
        
        messages.add(com.example.Project.dto.ChatMessage.builder()
                .role("user")
                .content(userContent)
                .build());

        return messages;
    }
}