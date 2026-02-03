package com.example.Project.service;

import com.example.Project.dto.ChatMessage;
import com.example.Project.dto.PdfExportRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    // 👇 [핵심] 이미 있는 저장소(서비스)를 가져다 씀!
    private final ChatContextService chatContextService; 

    public byte[] generatePdf(PdfExportRequest request) {
        // 👇 [핵심] DB 대신 chatContextService한테 "대화 내역 줘"라고 요청
        List<ChatMessage> history = chatContextService.getHistory(
                request.getSessionId(), 
                request.getObjectName() // PDF 요청할 때 어떤 부품인지도 알아야 함
        );

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 1. 폰트 설정
            BaseFont baseFont = BaseFont.createFont("HYGoThic-Medium", "UniKS-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font contentFont = new Font(baseFont, 11, Font.NORMAL);
            Font roleFontUser = new Font(baseFont, 10, Font.BOLD, BaseColor.BLUE);
            Font roleFontAi = new Font(baseFont, 10, Font.BOLD, new BaseColor(0, 100, 0));

            // 2. 제목
            document.add(new Paragraph(request.getTitle(), titleFont));
            document.add(new Paragraph(" ", contentFont));

            // 3. 메모
            if (request.getMemo() != null) {
                document.add(new Paragraph("📝 메모: " + request.getMemo(), contentFont));
                document.add(new Paragraph(" ", contentFont));
            }

            // 4. 대화 내용 출력 (Loop)
            for (ChatMessage msg : history) {
                // 역할에 따라 색깔 다르게
                String role = "user".equals(msg.getRole()) ? "[ 나 ]" : "[ AI ]";
                Font roleFont = "user".equals(msg.getRole()) ? roleFontUser : roleFontAi;

                document.add(new Paragraph(role, roleFont));
                document.add(new Paragraph(msg.getContent(), contentFont));
                document.add(new Paragraph(" ", contentFont)); // 줄바꿈
            }

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 실패: " + e.getMessage());
        }

        return out.toByteArray();
    }
}