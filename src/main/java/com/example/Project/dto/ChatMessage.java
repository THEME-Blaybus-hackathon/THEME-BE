package com.example.Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String role;
    private String content;
    private String htmlContent;

    public static ChatMessage system(String content) {
        return ChatMessage.builder()
                .role("system")
                .content(content)
                .build();
    }

    public static ChatMessage user(String content) {
        return ChatMessage.builder()
                .role("user")
                .content(content)
                .htmlContent(convertToHtml(content))
                .build();
    }

    public static ChatMessage assistant(String content) {
        return ChatMessage.builder()
                .role("assistant")
                .content(content)
                .htmlContent(convertToHtml(content))
                .build();
    }

    private static String convertToHtml(String text) {
        if (text == null) {
            return "";
        }

        String html = text;

        // 줄바꿈 → <br>
        html = html.replace("\n", "<br>");

        // **볼드** → <strong>
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");

        // *이탤릭* → <em>
        html = html.replaceAll("\\*(.*?)\\*", "<em>$1</em>");

        // 번호 목록 (1. 2. 3.)
        html = html.replaceAll("(\\d+)\\. ", "<br><strong>$1.</strong> ");

        // - 목록 → •
        html = html.replaceAll("- (.*?)<br>", "<br>• $1<br>");

        return html.trim();
    }
}
