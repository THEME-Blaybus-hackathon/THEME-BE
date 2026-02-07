package com.example.Project.dto;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class MemoResponse {
    private Long id;
    private String title;
    private String content;
    private String partName;
    private LocalDateTime createdAt;
}