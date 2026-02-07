package com.example.Project.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoUpdateRequest {
    private String content; // 수정할 메모 내용
}