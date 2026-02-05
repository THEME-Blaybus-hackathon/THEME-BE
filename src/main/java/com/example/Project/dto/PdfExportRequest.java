package com.example.Project.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfExportRequest {
    private String title;       // 문서 제목 (예: "드론 엔진 학습 리포트")
    private String sessionId;   // 세션 ID
    private String objectName;  // 3D 모델 이름 (예: "JET_ENGINE
    private List<ChatLog> chatHistory; // 대화 내용 리스트

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatLog {
        private String role;    // "user" or "assistant"
        private String content; // 대화 내용
    }
    
}

