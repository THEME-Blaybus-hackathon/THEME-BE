package com.example.Project.service;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Project.dto.MemoResponse;
import com.example.Project.dto.PdfExportRequest;
import com.example.Project.entity.QuizAnswer;
import com.example.Project.entity.User;
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
    private final QuizAnswerRepository quizAnswerRepository;
    private final AiSummaryService aiSummaryService;
    private final ChatService chatService;

    public byte[] generatePdf(PdfExportRequest request, User user) {

        String sessionId = request.getSessionId();
        String quizId = request.getQuizId();
        String objectName = request.getObjectName();
        String partName = request.getPartName();

        log.info("📄 PDF 생성 시작 - sessionId={}, quizId={}, object={}", sessionId, quizId, objectName);

        // =========================
        // 1. 메모 조회
        // =========================
        String savedMemo = "메모 내용 없음";
        try {
            List<MemoResponse> memos = memoService.getMemosByPart(user.getEmail(), partName);
            if (memos != null && !memos.isEmpty()) {
                savedMemo = memos.stream()
                        .map(MemoResponse::getContent)
                        .collect(Collectors.joining("\n- "));
                savedMemo = "- " + savedMemo;
            }
        } catch (Exception e) {
            log.warn("⚠️ 메모 조회 실패", e);
        }

        // =========================
        // 2. AI 요약
        // =========================
        String summaryText = "AI 요약을 생성할 수 없습니다.";
        try {
            String summary = aiSummaryService.createSummary(sessionId, user);
            if (summary != null && !summary.isBlank()) {
                summaryText = summary;
            }
        } catch (Exception e) {
            log.warn("⚠️ AI 요약 실패", e);
        }

        // =========================
        // 3. 퀴즈 조회
        // =========================
        List<QuizAnswer> quizList = Collections.emptyList();
        try {
            quizList = quizAnswerRepository
                    .findBySessionIdAndObjectNameOrderByCreatedAtDesc(quizId, objectName);
        } catch (Exception e) {
            log.warn("⚠️ 퀴즈 조회 실패", e);
        }

        // =========================
        // 4. PDF 생성
        // =========================
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // ---- 한글 폰트 (OS 독립, Render 안전) ----
            BaseFont baseFont;
            try {
                baseFont = BaseFont.createFont(
                        "font/NanumGothic.ttf",
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED
                );
            } catch (Exception e) {
                log.error("❌ PDF 한글 폰트 로드 실패", e);
                throw new RuntimeException("PDF 폰트 로드 실패");
            }

            Font titleFont = new Font(baseFont, 20, Font.BOLD);
            Font sectionFont = new Font(baseFont, 14, Font.BOLD, BaseColor.DARK_GRAY);
            Font bodyFont = new Font(baseFont, 10, Font.NORMAL);
            Font correctFont = new Font(baseFont, 10, Font.BOLD, BaseColor.BLUE);
            Font wrongFont = new Font(baseFont, 10, Font.BOLD, BaseColor.RED);

            // =========================
            // PDF 내용
            // =========================
            document.add(new Paragraph(
                    request.getTitle() != null ? request.getTitle() : "Learning Report",
                    titleFont
            ));
            document.add(new Paragraph("Target: " + objectName, bodyFont));
            document.add(new Paragraph("--------------------------------------------------", bodyFont));

            document.add(new Paragraph("\n1. My Memos", sectionFont));
            document.add(new Paragraph(savedMemo, bodyFont));

            document.add(new Paragraph("\n2. AI Summary", sectionFont));
            document.add(new Paragraph(summaryText, bodyFont));

            document.add(new Paragraph("\n3. Quiz Review", sectionFont));

            if (quizList != null && !quizList.isEmpty()) {
                for (int i = 0; i < quizList.size(); i++) {
                    QuizAnswer q = quizList.get(i);

                    document.add(new Paragraph(
                            "\nQ" + (i + 1) + ". " + q.getQuestion(),
                            bodyFont
                    ));

                    Paragraph result = new Paragraph(
                            "Your Answer: " + q.getUserAnswer()
                                    + " / Correct: " + q.getCorrectAnswer(),
                            bodyFont
                    );

                    if (q.isCorrect()) {
                        result.add(new Chunk(" [CORRECT]", correctFont));
                    } else {
                        result.add(new Chunk(" [WRONG]", wrongFont));
                    }

                    document.add(result);
                    document.add(new Paragraph(
                            "Explanation: " + q.getExplanation(),
                            new Font(baseFont, 9, Font.ITALIC)
                    ));
                }
            } else {
                document.add(new Paragraph("No quiz data available.", bodyFont));
            }

            document.close();
        } catch (Exception e) {
            log.error("❌ PDF 생성 중 치명적 오류", e);
            throw new RuntimeException("PDF 생성 실패");
        }

        return out.toByteArray();
    }
}