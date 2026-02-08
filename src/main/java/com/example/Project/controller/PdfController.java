package com.example.Project.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.PdfExportRequest;
import com.example.Project.service.PdfExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "PDF Export API", description = "학습 리포트 PDF 다운로드")
@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
@Slf4j
public class PdfController {

    private final PdfExportService pdfExportService;

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }
        return authentication.getName();
    }

    @Operation(summary = "PDF 리포트 다운로드", description = "요약, 퀴즈 결과, 대화 내용을 포함한 PDF를 생성하여 다운로드합니다.")
    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadReport(@RequestBody PdfExportRequest request) {
        log.info("PDF Download Request | session: {} | object: {}", request.getSessionId(), request.getObjectName());

        try {
            String email = getCurrentUserEmail();
            byte[] pdfFile = pdfExportService.generatePdf(email, request);

            String filename = "Report_" + request.getObjectName() + ".pdf";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfFile);

        } catch (Exception e) {
            log.error("PDF generation failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}