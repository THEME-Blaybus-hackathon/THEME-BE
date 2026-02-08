package com.example.Project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemoRequestDto {
    private String title;
    private String content;
    private String partName;
}