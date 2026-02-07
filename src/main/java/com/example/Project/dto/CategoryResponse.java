package com.example.Project.dto;

import java.util.List;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private String categoryName;
    private String mainImageUrl;
    private List<PartSummaryResponse> parts;
}