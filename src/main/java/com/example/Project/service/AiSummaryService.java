package com.example.Project.service;

import com.example.Project.entity.ChatMessage;
import com.example.Project.entity.ChatSession;
import com.example.Project.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AiSummaryService
 * AI 대화 요약 생성 서비스 (신형 DB 기반)
 * 
 * 기존 AiAssistantService.createSummary()를 신형 구조로 변환
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiSummaryService {

    private final ChatService chatService;
    private final OpenAiService openAiService;

    /**
     * 세션의 전체 대화 내용을 요약
     * 
     * @param sessionId 세션 ID
     * @param user 현재 사용자 (소유권 검증용)
     * @return 3줄 요약 (불렛포인트 형식)
     */
    @Transactional(readOnly = true)
    public String createSummary(String sessionId, User user) {
        log.info("Creating summary | sessionId: {} | user: {}", sessionId, user.getName());

        // 1. 세션 조회 + 소유권 검증
        ChatSession session = chatService.getSessionBySessionId(sessionId, user);
        String objectName = session.getObjectName();

        // 2. 전체 대화 히스토리 조회
        List<ChatMessage> messages = chatService.getSessionMessages(session);

        if (messages == null || messages.isEmpty()) {
            return "아직 대화 내용이 없습니다.";
        }

        // 3. 대화 내용을 문자열로 변환
        String chatLog = messages.stream()
                .map(msg -> msg.getRole() + ": " + msg.getContent())
                .collect(Collectors.joining("\n"));

        // 4. OpenAI 요약 요청
        String prompt = String.format(
            "다음은 사용자가 '%s' 3D 모델을 학습하며 AI 튜터와 나눈 대화이다.\n" +
            "이 대화에서 학습자가 배운 핵심 내용을 3줄로 요약해줘.\n" +
            "형식은 '- 내용' 스타일의 불렛포인트로 작성해줘.\n\n" +
            "[대화 내용]\n%s", 
            objectName, chatLog
        );

        log.info("Requesting summary from OpenAI | sessionId: {} | messageCount: {}", 
                sessionId, messages.size());

        String summary = openAiService.callGpt(prompt);

        log.info("Summary created | sessionId: {} | length: {}", sessionId, summary.length());

        return summary;
    }

    /**
     * objectName 기준으로 요약 생성 (하위 호환성)
     * 
     * @param sessionId 세션 ID
     * @param objectName 3D 모델명 (사용하지 않음, 세션에서 자동 추출)
     * @param user 현재 사용자
     * @return 3줄 요약
     * @deprecated objectName 파라미터는 무시되고 세션에서 자동 추출됩니다.
     */
    @Deprecated
    @Transactional(readOnly = true)
    public String createSummary(String sessionId, String objectName, User user) {
        // objectName 파라미터는 무시하고 세션에서 추출
        return createSummary(sessionId, user);
    }
}
