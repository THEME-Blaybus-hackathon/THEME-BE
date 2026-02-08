package com.example.Project.dto;

import com.example.Project.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 대화 히스토리 조회 Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryResponse {
    private String sessionId;
    private String objectName;
    private List<MessageDto> messages;
    private int totalMessages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDto {
        private Long id;
        private String role;
        private String content;
        private String selectedPart;
        private LocalDateTime createdAt;

        public static MessageDto from(ChatMessage message) {
            return MessageDto.builder()
                    .id(message.getId())
                    .role(message.getRole())
                    .content(message.getContent())
                    .selectedPart(message.getSelectedPart())
                    .createdAt(message.getCreatedAt())
                    .build();
        }
    }

    public static ChatHistoryResponse from(String sessionId, String objectName, List<ChatMessage> messages) {
        return ChatHistoryResponse.builder()
                .sessionId(sessionId)
                .objectName(objectName)
                .messages(messages.stream()
                        .map(MessageDto::from)
                        .collect(Collectors.toList()))
                .totalMessages(messages.size())
                .build();
    }
}
