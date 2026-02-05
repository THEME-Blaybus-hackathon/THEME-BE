package com.example.Project.service;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.example.Project.dto.PdfExportRequest;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfExportService {

    private final ChatContextService chatContextService;
    private final MemoService memoService;
    private final AiSummaryService aiSummaryService; // 👈 [변경] OpenAiService 대신 AiSummaryService 사용

    public byte[] generatePdf(PdfExportRequest request) {
        // 1. 메모 가져오기
        String savedMemo = memoService.getMemo(request.getObjectName());

        // 2. [변경] AiSummaryService를 통해 요약본 가져오기
        log.info("PDF 생성을 위한 AI 요약 요청 중... SessionId: {}", request.getSessionId());
        String summaryText = aiSummaryService.generateSummary(request.getSessionId(), request.getObjectName());
        
        // --- PDF 생성 시작 ---
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 폰트 설정 (한글 깨짐 방지)
            BaseFont baseFont;
            try {
                // 리눅스/서버 환경용 (폰트 파일이 없으면 에러날 수 있으므로 예외처리 필수)
                baseFont = BaseFont.createFont("HYGoThic-Medium", "UniKS-UCS2-H", BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                // 폰트가 없을 경우 기본 영문 폰트로 대체 (한글은 안 나오지만 에러는 안 나게)
                log.warn("한글 폰트 로드 실패, 기본 폰트를 사용합니다. (한글 미출력 가능성 있음)");
                baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }
            
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font sectionFont = new Font(baseFont, 14, Font.BOLD, BaseColor.DARK_GRAY);
            Font contentFont = new Font(baseFont, 11, Font.NORMAL);
            // 요약문이 너무 길 수 있으니 약간 작게 설정
            Font summaryFont = new Font(baseFont, 10, Font.NORMAL, BaseColor.BLACK); 

            // 1. 제목
            document.add(new Paragraph(request.getTitle(), titleFont));
            document.add(new Paragraph(" ", contentFont)); // 줄바꿈
            document.add(new Paragraph("------------------------------------------------", contentFont));
            document.add(new Paragraph(" ", contentFont));

            // 2. 사용자 학습 메모
            document.add(new Paragraph("📝 나의 학습 메모", sectionFont));
            document.add(new Paragraph(" ", contentFont));
            
            if (savedMemo != null && !savedMemo.trim().isEmpty()) {
                document.add(new Paragraph(savedMemo, contentFont));
            } else {
                document.add(new Paragraph("(작성된 메모가 없습니다)", contentFont));
            }
            
            document.add(new Paragraph(" ", contentFont));
            document.add(new Paragraph("------------------------------------------------", contentFont));
            document.add(new Paragraph(" ", contentFont));

            // 3. AI 학습 요약 리포트 (핵심 기능)
            document.add(new Paragraph("🤖 AI 학습 요약 리포트", sectionFont));
            document.add(new Paragraph(" ", contentFont));
            
            // 요약된 텍스트 출력
            document.add(new Paragraph(summaryText, summaryFont));

            document.close();
        } catch (Exception e) {
            log.error("PDF 생성 중 오류 발생", e);
            throw new RuntimeException("PDF 생성 실패: " + e.getMessage());
        }

        return out.toByteArray();
    }
}