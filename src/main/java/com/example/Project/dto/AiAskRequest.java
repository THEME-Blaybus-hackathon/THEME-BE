package com.example.Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAskRequest {

    private String sessionId;
    private String objectName; // JET_ENGINE | SUSPENSION | ROBOT_ARM | VICE
    private String question;
    private String selectedPart; // nullable - specific mesh/part name
}
