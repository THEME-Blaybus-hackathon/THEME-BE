package com.example.Project.service;

import java.util.List;

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

    public AiAskResponse processQuery(AiAskRequest request) {
        log.info("Processing AI query | session: {} | object: {} | part: {}",
                request.getSessionId(), request.getObjectName(), request.getSelectedPart());

        if (!promptService.isValidObjectId(request.getObjectName())) {
            return AiAskResponse.of("Invalid object ID: " + request.getObjectName()
                    + ". Supported: v4_engine, suspension, robot_gripper, robot_arm, machine_vice, leaf_spring, drone");
        }

        String systemPrompt = promptService.getSystemPrompt(request.getObjectName());

        List<ChatMessage> history = chatContextService.getHistory(
                request.getSessionId(),
                request.getObjectName()
        );

        List<ChatMessage> messages = openAiService.buildMessages(
                systemPrompt,
                history,
                request.getQuestion(),
                request.getSelectedPart()
        );

        // 5. Call OpenAI API
        String answer = openAiService.sendChatCompletion(messages);

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

        log.info("AI response generated | session: {} | object: {}",
                request.getSessionId(), request.getObjectName());

        return AiAskResponse.of(answer);
    }

    public void clearHistory(String sessionId, String objectId) {
        chatContextService.clearHistory(sessionId, objectId);
    }

    public void clearSession(String sessionId) {
        chatContextService.clearSession(sessionId);
    }
}
