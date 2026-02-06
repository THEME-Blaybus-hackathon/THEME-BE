package com.example.Project.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private String categoryName;    // 예: drone
    private String mainImageUrl;    // 예: /asset/drone/main_image.png (여기서만 나옴!)
    private List<PartSummaryResponse> parts; // 부품 목록 (ID, 이름)
}