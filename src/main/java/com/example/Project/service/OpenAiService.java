package com.example.Project.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.Project.dto.ChatMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model:gpt-5-mini}")
    private String model;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendChatCompletion(List<ChatMessage> messages) {
        return sendChatCompletion(messages, false);
    }

    public String sendChatCompletionForQuiz(List<ChatMessage> messages) {
        return sendChatCompletion(messages, true);
    }

    private String sendChatCompletion(List<ChatMessage> messages, boolean isQuizGeneration) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);

            if (model.startsWith("gpt-5") || model.contains("gpt-4o")) {
                if (isQuizGeneration) {
                    requestBody.put("max_completion_tokens", 5000);
                } else {
                    requestBody.put("max_completion_tokens", 3000);
                }
            } else {
                requestBody.put("max_tokens", 4096);
                requestBody.put("temperature", 0.7);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.debug("Sending request to OpenAI with {} messages", messages.size());

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null) {
                throw new RuntimeException("Empty response from OpenAI");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No choices in OpenAI response");
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");

            log.info("Received response from OpenAI ({} tokens)",
                    ((Map<String, Object>) responseBody.get("usage")).get("total_tokens"));

            return content;

        } catch (Exception e) {
            log.error("OpenAI API error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get AI response: " + e.getMessage(), e);
        }
    }

    public List<ChatMessage> buildMessages(String systemPrompt, List<ChatMessage> history, String userQuery, String selectedPart) {
        List<ChatMessage> messages = new ArrayList<>();

        String enhancedSystemPrompt = systemPrompt;
        if (selectedPart != null && !selectedPart.trim().isEmpty()) {
            enhancedSystemPrompt += "\n\nCRITICAL: User selected part '" + selectedPart
                    + "'. Answer MUST focus on THIS PART ONLY. Explain: 1) what it does, 2) why needed, 3) how it interacts. Keep 5-7 sentences.";
        } else {
            enhancedSystemPrompt += "\n\nNo part selected. Give brief system overview in 5-8 sentences. Be concise.";
        }
        messages.add(ChatMessage.system(enhancedSystemPrompt));

        int maxHistoryMessages = 4;
        int startIndex = Math.max(0, history.size() - maxHistoryMessages);
        messages.addAll(history.subList(startIndex, history.size()));

        String fullQuery = userQuery;
        if (selectedPart != null && !selectedPart.trim().isEmpty()) {
            fullQuery = "[SELECTED PART: " + selectedPart + "] " + userQuery;
        }
        messages.add(ChatMessage.user(fullQuery));

        return messages;
    }

    public String callGpt(String prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.user(prompt)); 
        return sendChatCompletion(messages, false);
    }
}