package com.example.Project.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.AiAskRequest;
import com.example.Project.dto.AiAskResponse;
import com.example.Project.dto.ApiResponse;
import com.example.Project.dto.ChatMessage;
import com.example.Project.service.AiAssistantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "AI Assistant API", description = "Context-aware AI assistant")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    @Operation(summary = "AI에게 질문")
    @PostMapping("/ask")
    public ResponseEntity<AiAskResponse> ask(@Valid @RequestBody AiAskRequest request) {
        try {
            AiAskResponse response = aiAssistantService.processQuery(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("AI Error", e);
            return ResponseEntity.internalServerError()
                    .body(AiAskResponse.of("Error: " + e.getMessage()));
        }
    }

    @Operation(summary = "학습 요약", description = "현재 세션의 대화 내용을 요약합니다.")
    @PostMapping("/summary")
    public ResponseEntity<?> generateSummary(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String objectName = request.get("objectName");
        
        log.info("Generating summary for session: {}", sessionId);
        
        String summary = aiAssistantService.createSummary(sessionId, objectName);
        
        return ResponseEntity.ok(Map.of("summary", summary));
    }

    @Operation(summary = "대화 기록 조회")
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(
            @RequestParam String sessionId,
            @RequestParam String objectName) {
        return ResponseEntity.ok(aiAssistantService.getChatHistory(sessionId, objectName));
    }

    @DeleteMapping("/history")
    public ResponseEntity<ApiResponse<String>> clearHistory(
            @RequestParam String sessionId,
            @RequestParam String objectId) {
        aiAssistantService.clearHistory(sessionId, objectId);
        return ResponseEntity.ok(ApiResponse.success("Cleared", "History cleared"));
    }

    @DeleteMapping("/session")
    public ResponseEntity<ApiResponse<String>> clearSession(@RequestParam String sessionId) {
        aiAssistantService.clearSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Cleared", "Session cleared"));
    }
}