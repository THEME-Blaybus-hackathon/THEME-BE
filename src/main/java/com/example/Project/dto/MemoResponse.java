package com.example.Project.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoResponse {
    private Long id;
    private String partName;
    private String content;
    private LocalDateTime createdAt;
}