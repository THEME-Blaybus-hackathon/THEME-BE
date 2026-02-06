package com.example.Project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Project.dto.AiAskRequest;
import com.example.Project.dto.AiAskResponse;
import com.example.Project.dto.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiAssistantService {

    private final PromptService promptService;
    private final ChatContextService chatContextService;
    private final OpenAiService openAiService;

    // 1. 질문 처리
    public AiAskResponse processQuery(AiAskRequest request) {
        log.info("Processing AI query | session: {} | object: {} | part: {}",
                request.getSessionId(), request.getObjectName(), request.getSelectedPart());

        if (!promptService.isValidObjectId(request.getObjectName())) {
            return AiAskResponse.of("Invalid object ID: " + request.getObjectName());
        }

        String systemPrompt = promptService.getSystemPrompt(request.getObjectName());

        // 대화 기록 조회
        List<ChatMessage> history = chatContextService.getHistory(
                request.getSessionId(),
                request.getObjectName()
        );

        // 메시지 구성 (OpenAiService의 buildMessages 사용)
        List<ChatMessage> messages = openAiService.buildMessages(
                systemPrompt,
                history,
                request.getQuestion(),
                request.getSelectedPart()
        );

        // GPT 호출
        String answer = openAiService.sendChatCompletion(messages);

        // 대화 내용 저장
        chatContextService.addUserMessage(
                request.getSessionId(),
                request.getObjectName(),
                request.getQuestion()
        );
        chatContextService.addAssistantMessage(
                request.getSessionId(),
                request.getObjectName(),
                answer
        );

        return AiAskResponse.of(answer);
    }

    // 2. 학습 요약 기능
    public String createSummary(String sessionId, String objectName) {
        List<ChatMessage> history = chatContextService.getHistory(sessionId, objectName);

        if (history == null || history.isEmpty()) {
            return "아직 대화 내용이 없습니다.";
        }

        // 대화 내용을 문자열로 합치기
        // (ChatMessage 내부에 getRole(), getContent() 메서드가 있다고 가정)
        String chatLog = history.stream()
                .map(msg -> msg.getRole() + ": " + msg.getContent())
                .collect(Collectors.joining("\n"));

        String prompt = String.format(
            "다음은 사용자가 '%s' 3D 모델을 학습하며 AI 튜터와 나눈 대화이다.\n" +
            "이 대화에서 학습자가 배운 핵심 내용을 3줄로 요약해줘.\n" +
            "형식은 '- 내용' 스타일의 불렛포인트로 작성해줘.\n\n" +
            "[대화 내용]\n%s", 
            objectName, chatLog
        );

        // OpenAiService에 추가한 callGpt 호출
        return openAiService.callGpt(prompt);
    }

    // 3. 기록 조회
    public List<ChatMessage> getChatHistory(String sessionId, String objectName) {
        return chatContextService.getHistory(sessionId, objectName);
    }

    // 4. 기록 삭제
    public void clearHistory(String sessionId, String objectId) {
        chatContextService.clearHistory(sessionId, objectId);
    }

    public void clearSession(String sessionId) {
        chatContextService.clearSession(sessionId);
    }
}