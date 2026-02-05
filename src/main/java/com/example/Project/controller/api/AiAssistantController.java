package com.example.Project.controller.api;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import com.example.Project.dto.PdfExportRequest;
import com.example.Project.service.AiAssistantService;
import com.example.Project.service.PdfExportService;

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
    private final PdfExportService pdfExportService;

    @Operation(summary = "AI에게 질문 (HTML 응답)", description = "3D 모델에 대해 AI에게 질문하고 HTML 형식 답변 받기")
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

    @Operation(summary = "대화 기록 조회", description = "세션의 전체 대화 기록 조회 (ChatGPT 스타일)")
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(
            @RequestParam String sessionId,
            @RequestParam String objectName
    ) {
        log.info("Get chat history | session: {} | object: {}", sessionId, objectName);
        List<ChatMessage> history = aiAssistantService.getChatHistory(sessionId, objectName);
        return ResponseEntity.ok(history);
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

    @Operation(summary = "Export Chat & Memo to PDF", description = "Generate a PDF report containing user memo and chat history.")
    @PostMapping("/report")
    public ResponseEntity<byte[]> generateReport(@RequestBody PdfExportRequest request) {
        log.info("PDF Generation Request | title: {}", request.getTitle());

        byte[] pdfBytes = pdfExportService.generatePdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDispositionFormData("attachment", "report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
