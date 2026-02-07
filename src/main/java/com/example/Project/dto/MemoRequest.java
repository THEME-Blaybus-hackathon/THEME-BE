package com.example.Project.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoRequest {
    private String partName; // 어떤 부품에 남기는 메모인지
    private String content;  // 메모 내용
}