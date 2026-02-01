package com.example.Project.dto; 

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearningObjectResponse {
    private Long id;            // 오브젝트 고유 ID (1, 2, 3...)
    private String name;        // 이름 (예: Jet Engine)
    private String description; // 설명 (예: 제트 엔진의 원리...)
    private String imageUrl;    // 썸네일 이미지 주소
    private String modelUrl;    // 3D 모델 파일(.glb) 주소
    private String category;    // 카테고리 (예: Engine Parts, Transmission Parts...)
}