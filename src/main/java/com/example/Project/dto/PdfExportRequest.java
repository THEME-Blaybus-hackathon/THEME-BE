package com.example.Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfExportRequest {
    private String title;
    private String sessionId; 
    private String objectName; 

}