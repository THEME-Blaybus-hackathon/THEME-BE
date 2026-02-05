package com.example.Project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Project.dto.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiSummaryService {

    private final OpenAiService openAiService;
    private final ChatContextService chatContextService;

    /**
     * 특정 세션의 대화 내역을 기반으로 요약본 생성
     */
    public String generateSummary(String sessionId, String objectName) {
        // 1. 대화 내역 가져오기
        List<ChatMessage> history = chatContextService.getHistory(sessionId, objectName);
        if (history == null || history.isEmpty()) {
            return "대화 내역이 없어 요약할 내용이 없습니다.";
        }

        // 2. 대화 내역을 텍스트로 변환
        String chatContext = history.stream()
                .map(msg -> msg.getRole() + ": " + msg.getContent())
                .collect(Collectors.joining("\n"));

        // 3. 요약 전용 프롬프트 작성
        String prompt = """
            아래는 사용자와 AI의 3D 모델 학습 대화 내역이야.
            이 내용을 바탕으로 '학습 요점 정리 리포트'를 만들어줘.
            
            [규칙]
            1. 사용자가 질문한 핵심 주제들을 파악해서 Bullet Point(•)로 정리해.
            2. 불필요한 인사말이나 잡담은 빼고, 공학적 지식 위주로 요약해.
            3. 말투는 '~함', '~임' 같은 개조식이나 '합니다' 체로 깔끔하게.
            4. 한국어로 작성해.
            
            [대화 내역]
            %s
            """.formatted(chatContext);

        // 4. 메시지 구성
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.system("당신은 공학 교육 리포트 요약 전문가입니다."));
        messages.add(ChatMessage.user(prompt));

        // 5. OpenAiService 호출
        log.info("Generating summary for session: {}", sessionId);
        return openAiService.sendChatCompletion(messages);
    }
}