package com.example.Project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AI Chat API Request DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 채팅 요청")
public class AiChatRequest {
    
    @Schema(description = "세션 ID (기존 대화 이어가기 시 필수)", example = "20260208-001", nullable = true)
    private String sessionId;
    
    @Schema(description = "학습 객체명 (새 세션 생성 시 필수, 기존 세션 사용 시 생략 가능)", 
            example = "drone", nullable = true)
    private String objectName;
    
    @Schema(description = "사용자 질문", example = "드론의 메인 프레임은 어떤 역할을 하나요?", required = true)
    private String message;
    
    @Schema(description = "선택한 부품명 (선택사항)", example = "main_frame_mir", nullable = true)
    private String selectedPart;
}
