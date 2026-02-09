package com.example.Project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI Chat API Response DTO
 */
@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 채팅 응답")
public class AiChatResponse {
    
    @Schema(description = "세션 ID", example = "20260208-001")
    private String sessionId;
    
    @Schema(description = "AI 답변", example = "드론의 메인 프레임은 드론의 골격 역할을 합니다...")
    private String answer;
    
    @Schema(description = "새 세션 여부", example = "true")
    private boolean isNewSession;
    
    @Schema(description = "메시지 ID", example = "123")
    private Long messageId;

    public static AiChatResponse of(String sessionId, String answer, boolean isNewSession, Long messageId) {
        return AiChatResponse.builder()
                .sessionId(sessionId)
                .answer(answer)
                .isNewSession(isNewSession)
                .messageId(messageId)
                .build();
    }
}
