package com.example.Project.service;

import java.io.ByteArrayOutputStream;
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
    private final AiSummaryService aiSummaryService;
    private final QuizAnswerRepository quizAnswerRepository; // 👈 [주입]

    public byte[] generatePdf(PdfExportRequest request) {
        // 1. 데이터 조회
        String savedMemo = memoService.getMemo(request.getObjectName());
        String summaryText = aiSummaryService.generateSummary(request.getSessionId(), request.getObjectName());
        
        // 2. 퀴즈 기록 조회
        List<QuizAnswer> quizList = quizAnswerRepository
                .findBySessionIdAndObjectNameOrderByCreatedAtDesc(request.getSessionId(), request.getObjectName());

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- 폰트 설정 (한글) ---
            BaseFont baseFont;
            try {
                baseFont = BaseFont.createFont("HYGoThic-Medium", "UniKS-UCS2-H", BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }
            
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 14, Font.BOLD, BaseColor.DARK_GRAY);
            Font bodyFont = new Font(baseFont, 11, Font.NORMAL);
            Font correctColor = new Font(baseFont, 11, Font.BOLD, BaseColor.BLUE);
            Font wrongColor = new Font(baseFont, 11, Font.BOLD, BaseColor.RED);

            // --- 1. 제목 ---
            document.add(new Paragraph(request.getTitle(), titleFont));
            document.add(new Paragraph(" ", bodyFont));
            document.add(new Paragraph("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", bodyFont));

            // --- 2. 메모 ---
            document.add(new Paragraph("\n 1. 나의 학습 메모", headerFont));
            document.add(new Paragraph(savedMemo != null && !savedMemo.isEmpty() ? savedMemo : "(없음)", bodyFont));

            // --- 3. AI 요약 ---
            document.add(new Paragraph("\n 2. AI 학습 요약", headerFont));
            document.add(new Paragraph(summaryText, bodyFont));

            // --- 4. 퀴즈 오답 노트 (NEW) ---
            document.add(new Paragraph("\n 3. 퀴즈 오답 노트 & 결과", headerFont));
            
            if (quizList != null && !quizList.isEmpty()) {
                int qNum = 1;
                for (QuizAnswer q : quizList) {
                    // Q. 문제
                    document.add(new Paragraph("\nQ" + qNum++ + ". " + q.getQuestion(), new Font(baseFont, 11, Font.BOLD)));
                    
                    // 결과 표시 (내답 vs 정답)
                    Paragraph resultP = new Paragraph();
                    resultP.setFont(bodyFont);
                    resultP.add("선택: " + q.getUserAnswer() + " / 정답: " + q.getCorrectAnswer() + "   ");
                    
                    if (q.isCorrect()) {
                        resultP.add(new Chunk("[Correct]", correctColor));
                    } else {
                        resultP.add(new Chunk("[Wrong]", wrongColor));
                    }
                    document.add(resultP);
                    
                    // 해설
                    document.add(new Paragraph("💡 " + q.getExplanation(), new Font(baseFont, 10, Font.ITALIC, BaseColor.DARK_GRAY)));
                    document.add(new Paragraph(" ", bodyFont));
                }
            } else {
                document.add(new Paragraph("\n(이 세션에서 푼 퀴즈 내역이 없습니다.)", bodyFont));
            }

            document.close();

        } catch (Exception e) {
            log.error("PDF Fail", e);
            throw new RuntimeException("PDF 생성 실패");
        }

        return out.toByteArray();
    }
}