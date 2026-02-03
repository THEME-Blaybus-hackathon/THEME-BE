package com.example.Project.dto;

import java.time.LocalDateTime;

import com.example.Project.entity.UserProgress;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "학습 진도 응답")
public class UserProgressResponse {

    @Schema(description = "진도 ID", example = "1")
    private Long id;

    @Schema(description = "3D 모델명", example = "v4_engine")
    private String objectName;

    @Schema(description = "마지막 본 부품", example = "piston")
    private String lastViewedPart;

    @Schema(description = "마지막 접근 시간", example = "2024-01-01T10:00:00")
    private LocalDateTime lastAccessedAt;

    @Schema(description = "총 방문 횟수", example = "5")
    private Integer visitCount;

    public static UserProgressResponse from(UserProgress progress) {
        return UserProgressResponse.builder()
                .id(progress.getId())
                .objectName(progress.getObjectName())
                .lastViewedPart(progress.getLastViewedPart())
                .lastAccessedAt(progress.getLastAccessedAt())
                .visitCount(progress.getVisitCount())
                .build();
    }
}
