package com.example.Project.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.AiAskRequest;
import com.example.Project.dto.AiAskResponse;
import com.example.Project.dto.ApiResponse;
import com.example.Project.service.AiAssistantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "AI Assistant API", description = "Context-aware AI assistant for 3D engineering models")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    @Operation(
            summary = "Ask AI about 3D model",
            description = "Send a question about a specific 3D engineering model. "
            + "Supports: JET_ENGINE, SUSPENSION, ROBOT_ARM, VICE. "
            + "Optional selectedPart parameter for part-specific explanations."
    )
    @PostMapping("/ask")
    public ResponseEntity<AiAskResponse> ask(@Valid @RequestBody AiAskRequest request) {
        log.info("AI Ask Request | session: {} | object: {} | query: '{}'",
                request.getSessionId(), request.getObjectName(),
                request.getQuestion().substring(0, Math.min(50, request.getQuestion().length())));

        try {
            AiAskResponse response = aiAssistantService.processQuery(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing AI request", e);
            return ResponseEntity.internalServerError()
                    .body(AiAskResponse.of("Sorry, I encountered an error: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Clear chat history",
            description = "Clear chat history for a specific session and object"
    )
    @DeleteMapping("/history")
    public ResponseEntity<ApiResponse<String>> clearHistory(
            @RequestParam String sessionId,
            @RequestParam String objectId) {
        log.info("Clear History Request | session: {} | object: {}", sessionId, objectId);

        aiAssistantService.clearHistory(sessionId, objectId);
        return ResponseEntity.ok(ApiResponse.success("History cleared", "Chat history cleared successfully"));
    }

    @Operation(
            summary = "Clear entire session",
            description = "Clear all chat history for a session (all objects)"
    )
    @DeleteMapping("/session")
    public ResponseEntity<ApiResponse<String>> clearSession(@RequestParam String sessionId) {
        log.info("Clear Session Request | session: {}", sessionId);

        aiAssistantService.clearSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Session cleared", "All chat history cleared for this session"));
    }
}
