package com.example.Project.dto;

import java.time.LocalDateTime;

import com.example.Project.entity.Memo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoResponse {
    private Long id;
    private String title;
    private String content;
    private String partName;
    private LocalDateTime createdAt;

    public MemoResponse(Memo memo) {
        this.id = memo.getId();
        this.title = memo.getTitle();
        this.content = memo.getContent();
        this.partName = memo.getPartName();
        this.createdAt = memo.getCreatedAt();
    }
}