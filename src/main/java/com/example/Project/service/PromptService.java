package com.example.Project.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PromptService {

    private Map<String, Map<String, String>> prompts;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void loadPrompts() {
        try (InputStream is = getClass().getResourceAsStream("/prompts.json")) {
            if (is == null) {
                throw new RuntimeException("prompts.json not found in resources");
            }
            prompts = objectMapper.readValue(is, Map.class);
            log.info("Loaded prompts for {} objects", prompts.keySet());
        } catch (IOException e) {
            log.error("Failed to load prompts.json", e);
            throw new RuntimeException("Failed to load prompts", e);
        }
    }

    public String getSystemPrompt(String objectId) {
        if (prompts == null || !prompts.containsKey(objectId)) {
            log.warn("Unknown objectId: {}. Using default prompt.", objectId);
            return "You are a helpful engineering tutor. Answer questions clearly and technically.";
        }
        return prompts.get(objectId).get("systemPrompt");
    }

    public boolean isValidObjectId(String objectId) {
        return prompts != null && prompts.containsKey(objectId);
    }
}
