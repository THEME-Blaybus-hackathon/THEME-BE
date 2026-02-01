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
    private String timestamp; // ISO-8601 format

    public static AiAskResponse of(String answer) {
        return AiAskResponse.builder()
                .answer(answer)
                .timestamp(Instant.now().toString())
                .build();
    }
}
