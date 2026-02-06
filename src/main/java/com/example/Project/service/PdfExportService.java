package com.example.Project.service;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Project.dto.PdfExportRequest;
import com.example.Project.entity.QuizAnswer;
import com.example.Project.repository.QuizAnswerRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
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

    private final MemoService memoService;
    private final AiAssistantService aiAssistantService;
    private final QuizAnswerRepository quizAnswerRepository;

    public byte[] generatePdf(PdfExportRequest request) {
        String sessionId = request.getSessionId();
        String objectName = request.getObjectName();

        log.info("PDF 생성 시작 - 세션: {}, 모델: {}", sessionId, objectName);

        // 1. [데이터 조회] (에러 방지를 위한 Null 처리)
        String savedMemo = "메모 내용 없음";
        try {
            String memo = memoService.getMemo(objectName);
            if (memo != null) savedMemo = memo;
        } catch (Exception e) {
            log.warn("메모 조회 실패 (PDF 생성은 계속 진행): {}", e.getMessage());
        }

        String summaryText = "AI 요약 생성 실패";
        try {
            String summary = aiAssistantService.createSummary(sessionId, objectName);
            if (summary != null) summaryText = summary;
        } catch (Exception e) {
            log.warn("AI 요약 실패 (PDF 생성은 계속 진행): {}", e.getMessage());
        }
        
        List<QuizAnswer> quizList = Collections.emptyList();
        try {
            quizList = quizAnswerRepository.findBySessionIdAndObjectNameOrderByCreatedAtDesc(sessionId, objectName);
        } catch (Exception e) {
            log.warn("퀴즈 조회 실패: {}", e.getMessage());
        }

        // 2. [PDF 생성]
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- 폰트 설정 (핵심: 에러 방지) ---
            BaseFont baseFont;
            try {
                // 1순위: 한글 폰트 시도 (itext-asian 의존성이 있거나, 시스템에 폰트가 있을 경우)
                baseFont = BaseFont.createFont("HYGoThic-Medium", "UniKS-UCS2-H", BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                log.error("한글 폰트 로딩 실패! 기본 영문 폰트로 대체합니다. (한글이 깨질 수 있음)", e);
                // 2순위: 무조건 되는 영문 폰트 (500 에러 방지용)
                baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }
            
            Font titleFont = new Font(baseFont, 20, Font.BOLD);
            Font sectionFont = new Font(baseFont, 16, Font.BOLD, BaseColor.DARK_GRAY);
            Font bodyFont = new Font(baseFont, 11, Font.NORMAL);
            Font correctColor = new Font(baseFont, 11, Font.BOLD, BaseColor.BLUE);
            Font wrongColor = new Font(baseFont, 11, Font.BOLD, BaseColor.RED);

            // [1] 제목
            document.add(new Paragraph(request.getTitle() != null ? request.getTitle() : "학습 리포트", titleFont));
            document.add(new Paragraph("Target Object: " + objectName, bodyFont));
            document.add(new Paragraph("--------------------------------------------------", bodyFont));

            // [2] 학습 메모
            document.add(new Paragraph("\n1. My Memo", sectionFont));
            document.add(new Paragraph(savedMemo, bodyFont));

            // [3] AI 요약
            document.add(new Paragraph("\n2. AI Summary", sectionFont));
            document.add(new Paragraph(summaryText, bodyFont));

            // [4] 퀴즈 오답 노트
            document.add(new Paragraph("\n3. Quiz Results (" + (quizList != null ? quizList.size() : 0) + ")", sectionFont));
            
            if (quizList != null && !quizList.isEmpty()) {
                int qNum = 1;
                for (QuizAnswer q : quizList) {
                    // Q. 문제
                    document.add(new Paragraph("\nQ" + qNum++ + ". " + q.getQuestion(), new Font(baseFont, 11, Font.BOLD)));
                    
                    // 결과
                    Paragraph resultP = new Paragraph();
                    resultP.setFont(bodyFont);
                    resultP.add("User: " + q.getUserAnswer() + "  /  Answer: " + q.getCorrectAnswer() + "   ");
                    
                    if (q.isCorrect()) {
                        resultP.add(new Chunk("[Correct]", correctColor));
                    } else {
                        resultP.add(new Chunk("[Wrong]", wrongColor));
                    }
                    document.add(resultP);
                    
                    // 해설
                    document.add(new Paragraph("Tip: " + q.getExplanation(), new Font(baseFont, 10, Font.ITALIC, BaseColor.DARK_GRAY)));
                    document.add(new Paragraph("-------------------------------------", new Font(baseFont, 8, Font.NORMAL, BaseColor.LIGHT_GRAY)));
                }
            } else {
                document.add(new Paragraph("(No quiz history found)", bodyFont));
            }

            document.close();
            log.info("PDF 생성 성공!");

        } catch (Exception e) {
            log.error("PDF 생성 중 치명적 오류 발생", e);
            throw new RuntimeException("PDF 생성 실패: " + e.getMessage());
        }

        return out.toByteArray();
    }
}