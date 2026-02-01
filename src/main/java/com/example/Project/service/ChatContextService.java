package com.example.Project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.Project.dto.ChatMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatContextService {

    private static final int MAX_HISTORY_SIZE = 3; // 최근 3개 Q&A만 유지 (토큰 절약)
    private final Map<String, Map<String, List<ChatMessage>>> chatHistory = new ConcurrentHashMap<>();

    public List<ChatMessage> getHistory(String sessionId, String objectId) {
        return chatHistory
                .computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(objectId, k -> new ArrayList<>());
    }

    public void addUserMessage(String sessionId, String objectId, String message) {
        List<ChatMessage> history = getHistory(sessionId, objectId);
        history.add(ChatMessage.user(message));
        trimHistory(history);
        log.debug("Added user message to session: {} | object: {}", sessionId, objectId);
    }

    public void addAssistantMessage(String sessionId, String objectId, String message) {
        List<ChatMessage> history = getHistory(sessionId, objectId);
        history.add(ChatMessage.assistant(message));
        trimHistory(history);
        log.debug("Added assistant message to session: {} | object: {}", sessionId, objectId);
    }

    private void trimHistory(List<ChatMessage> history) {
        while (history.size() > MAX_HISTORY_SIZE * 2) {
            history.remove(0);
        }
    }

    public void clearHistory(String sessionId, String objectId) {
        Map<String, List<ChatMessage>> sessionHistory = chatHistory.get(sessionId);
        if (sessionHistory != null) {
            sessionHistory.remove(objectId);
            log.info("Cleared history for session: {} | object: {}", sessionId, objectId);
        }
    }

    public void clearSession(String sessionId) {
        chatHistory.remove(sessionId);
        log.info("Cleared all history for session: {}", sessionId);
    }
}
