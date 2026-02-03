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
    private String objectName; // v4_engine | suspension | robot_gripper | robot_arm | machine_vice | leaf_spring | drone
    private String question;
    private String selectedPart; // nullable - specific mesh/part name
}
