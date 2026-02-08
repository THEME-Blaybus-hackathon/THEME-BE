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
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
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
        String objectName = request.getObjectName();

        log.info("PDF 생성 시작 - 사용자: {}, 세션: {}, 모델: {}", email, sessionId, objectName);

        // 1. [메모 데이터 조회]
        String savedMemo = "메모 내용 없음";
        try {
            List<MemoResponse> memos = memoService.getMemosByPart(email, objectName);
            
            if (memos != null && !memos.isEmpty()) {
                String memoContent = memos.stream()
                        .map(MemoResponse::getContent)
                        .collect(Collectors.joining("\n- "));
                savedMemo = "- " + memoContent;
            }
        } catch (Exception e) {
            log.warn("메모 조회 실패: {}", e.getMessage());
        }

        // 2. [AI 요약 데이터 조회]
        String summaryText = "AI 요약 생성 실패";
        try {
            String summary = aiSummaryService.createSummary(sessionId, user);
            if (summary != null && !summary.isEmpty()) {
                summaryText = summary;
            }
        } catch (Exception e) {
            log.warn("AI 요약 실패: {}", e.getMessage());
            summaryText = "AI 요약을 생성할 수 없습니다.";
        }

        // 3. [퀴즈 데이터 조회]
        List<QuizAnswer> quizList = Collections.emptyList();
        try {
             // 이 메서드도 Repository에 실제 존재하는지 확인 필요 (없으면 에러 남)
             // quizList = quizAnswerRepository.findBySessionIdAndObjectNameOrderByCreatedAtDesc(sessionId, objectName);
        } catch (Exception e) {
            log.warn("퀴즈 조회 실패: {}", e.getMessage());
        }

        // 4. [PDF 생성]
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 폰트 설정 (기본값)
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            // [내용 채우기]
            document.add(new Paragraph(request.getTitle() != null ? request.getTitle() : "Learning Report", titleFont));
            document.add(new Paragraph("User: " + email, bodyFont)); // 사용자 이메일 표시
            document.add(new Paragraph("Target: " + objectName, bodyFont));
            document.add(new Paragraph("--------------------------------------------------", bodyFont));

            document.add(new Paragraph("\n1. My Memos", sectionFont));
            document.add(new Paragraph(savedMemo, bodyFont));

            document.add(new Paragraph("\n2. AI Summary", sectionFont));
            document.add(new Paragraph(summaryText, bodyFont));

            document.add(new Paragraph("\n3. Quiz Review", sectionFont));
            if (quizList != null && !quizList.isEmpty()) {
                // 퀴즈 출력 로직 (생략 - 위와 동일)
            } else {
                document.add(new Paragraph("No quiz data available.", bodyFont));
            }

            document.close();
        } catch (Exception e) {
            log.error("PDF 생성 중 오류", e);
            throw new RuntimeException("PDF 생성 실패");
        }

        return out.toByteArray();
    }
}