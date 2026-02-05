package com.example.Project.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAskResponse {

    private String answer;
    private String htmlAnswer;
    private String timestamp;

    public static AiAskResponse of(String answer) {
        return AiAskResponse.builder()
                .answer(answer)
                .htmlAnswer(convertToHtml(answer))
                .timestamp(Instant.now().toString())
                .build();
    }

    private static String convertToHtml(String text) {
        if (text == null) {
            return "";
        }

        String html = text;

        html = html.replace("\n", "<br>");
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        html = html.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
        html = html.replaceAll("(\\d+)\\. ", "<br><strong>$1.</strong> ");
        html = html.replaceAll("- (.*?)<br>", "<br>• $1<br>");

        return html.trim();
    }
}
