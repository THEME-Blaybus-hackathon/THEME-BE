package com.example.Project.controller;

import com.example.Project.repository.LearningObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/db-status")
public class DatabaseStatusController {

    @Autowired
    private LearningObjectRepository learningObjectRepository;

    @GetMapping
    public Map<String, Object> getDatabaseStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            long totalObjects = learningObjectRepository.count();
            status.put("status", "connected");
            status.put("totalLearningObjects", totalObjects);
            status.put("message", totalObjects > 0 
                ? "✅ 데이터베이스에 " + totalObjects + "개의 부품이 있습니다."
                : "⚠️ 데이터베이스가 비어있습니다. data.sql을 실행해야 합니다.");
            
            if (totalObjects > 0) {
                // 첫 번째 부품 정보 확인
                var firstObject = learningObjectRepository.findById(1L);
                if (firstObject.isPresent()) {
                    var obj = firstObject.get();
                    Map<String, Object> sample = new HashMap<>();
                    sample.put("id", obj.getId());
                    sample.put("name", obj.getName());
                    sample.put("meshName", obj.getMeshName());
                    sample.put("modelUrl", obj.getModelUrl());
                    status.put("sampleData", sample);
                }
            }
        } catch (Exception e) {
            status.put("status", "error");
            status.put("message", "❌ 오류: " + e.getMessage());
        }
        
        return status;
    }
}
