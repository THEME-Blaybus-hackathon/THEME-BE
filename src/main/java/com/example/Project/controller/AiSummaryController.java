package com.example.Project.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.PdfExportRequest;
import com.example.Project.service.AiSummaryService;
import com.example.Project.service.PdfExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Summary & Report", description = "학습 요약 및 PDF 리포트 관련 API")
@Slf4j
public class AiSummaryController {

    private final AiSummaryService aiSummaryService;
    private final PdfExportService pdfExportService;

    // 1. AI 학습 요약 가져오기 (Text Only)
    @PostMapping("/summary")
    @Operation(summary = "대화 내용 요약하기 (텍스트)", description = "현재 세션의 대화 내용을 바탕으로 학습 요점을 텍스트로 반환합니다.")
    public ResponseEntity<Map<String, String>> getSummary(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String objectName = request.get("objectName");

        log.info("Requesting summary for Session: {}, Object: {}", sessionId, objectName);

        // 서비스 호출
        String summary = aiSummaryService.generateSummary(sessionId, objectName);

        return ResponseEntity.ok(Map.of("summary", summary));
    }

    // 2. PDF 리포트 다운로드 (Chat + Memo + Summary)
    @PostMapping("/report")
    @Operation(summary = "PDF 리포트 다운로드", description = "AI 요약본과 사용자의 메모를 포함한 PDF 리포트를 생성합니다.")
    public ResponseEntity<byte[]> downloadReport(@RequestBody PdfExportRequest request) {
        
        log.info("Generating PDF for Session: {}", request.getSessionId());

        // PDF 생성 (내부적으로 AiSummaryService를 사용함)
        byte[] pdfBytes = pdfExportService.generatePdf(request);

        // 파일 다운로드 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "report.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}