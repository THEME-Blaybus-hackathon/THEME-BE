package com.example.Project.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearningObjectResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String modelUrl;
    private String assemblyUrl; // 조립본 GLB 파일 ({partname}.glb)
    private String category;
}