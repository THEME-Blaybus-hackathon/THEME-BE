package com.example.Project.service;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Project.dto.ChatMessage;
import com.example.Project.dto.PdfExportRequest;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private final ChatContextService chatContextService;
    private final MemoService memoService;

    public byte[] generatePdf(PdfExportRequest request) {
        // [디버깅 로그] 요청 데이터 확인
        System.out.println("====== [PDF 생성 시작] ======");
        System.out.println("1. 요청 SessionID: " + request.getSessionId());
        System.out.println("2. 요청 부품명(ObjectName): " + request.getObjectName());

        // 1. 대화 내역 가져오기 (Null 방지)
        List<ChatMessage> history = chatContextService.getHistory(request.getSessionId(), request.getObjectName());
        if (history == null) history = Collections.emptyList();
        System.out.println("3. 가져온 대화 개수: " + history.size() + "개");

        // 2. DB에서 메모 가져오기
        String savedMemo = memoService.getMemo(request.getObjectName());
        System.out.println("4. DB에서 찾은 메모: " + savedMemo);

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // ==========================================
            // [중요] 맥북/서버 호환 폰트 설정 (수정됨)
            // ==========================================
            BaseFont baseFont;
            try {
                // 1순위: 한글 폰트 시도 (맥/윈도우 공통으로 있는 폰트가 없어서, 보통 파일을 넣어써야 함)
                // 임시로 iText 기본 한글 설정 시도
                baseFont = BaseFont.createFont("HYGoThic-Medium", "UniKS-UCS2-H", BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                // 폰트 없으면 에러 내지 말고, 그냥 영어 기본 폰트(Helvetica) 사용
                System.out.println("⚠️ 한글 폰트 로드 실패! 기본 영어 폰트로 대체합니다. (한글은 안 나옴)");
                baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }
            
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font contentFont = new Font(baseFont, 11, Font.NORMAL);
            Font roleFontUser = new Font(baseFont, 10, Font.BOLD, BaseColor.BLUE);
            Font roleFontAi = new Font(baseFont, 10, Font.BOLD, new BaseColor(0, 100, 0));

            // 제목
            document.add(new Paragraph(request.getTitle(), titleFont));
            document.add(new Paragraph(" ", contentFont));

            // 메모 출력
            document.add(new Paragraph("[ MEMO ]", titleFont));
            if (savedMemo != null && !savedMemo.trim().isEmpty()) {
                document.add(new Paragraph(savedMemo, contentFont));
            } else {
                document.add(new Paragraph("(저장된 메모가 없습니다)", contentFont));
            }
            document.add(new Paragraph(" ", contentFont));

            // 대화 내용 출력
            document.add(new Paragraph("[ CHAT HISTORY ]", titleFont));
            for (ChatMessage msg : history) {
                String role = "user".equals(msg.getRole()) ? "[ 나 ]" : "[ AI ]";
                Font roleFont = "user".equals(msg.getRole()) ? roleFontUser : roleFontAi;

                document.add(new Paragraph(role, roleFont));
                document.add(new Paragraph(msg.getContent(), contentFont));
                document.add(new Paragraph(" ", contentFont));
            }

            document.close();
            System.out.println("====== [PDF 생성 완료] ======");

        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 출력
            throw new RuntimeException("PDF 생성 실패: " + e.getMessage());
        }

        return out.toByteArray();
    }
}