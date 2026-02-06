package com.example.Project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.CategoryResponse; // [추가]
import com.example.Project.dto.LearningObjectResponse;
import com.example.Project.service.ObjectDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/objects")
@RequiredArgsConstructor
@Slf4j
public class LearningObjectController {

    private final ObjectDataService objectDataService;

    // 1. 카테고리 목록 조회
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        try {
            log.info("카테고리 목록 요청 들어옴");
            List<String> categories = objectDataService.getAllCategories();
            return ResponseEntity.ok(Map.of("count", categories.size(), "categories", categories));
        } catch (Exception e) {
            log.error("카테고리 조회 중 에러: ", e);
            return ResponseEntity.internalServerError().body("Server Error: " + e.getMessage());
        }
    }

    // 2. [수정됨] 모델(카테고리) 검색 -> 대표 이미지 포함
    @GetMapping
    public ResponseEntity<?> getPartsList(@RequestParam String category) {
        try {
            log.info("모델 검색 요청: {}", category);
            
            // 이제 CategoryResponse(이미지 포함)를 반환합니다.
            CategoryResponse response = objectDataService.getPartsByCategory(category);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("모델 검색 중 에러: ", e);
            return ResponseEntity.internalServerError().body("Server Error: " + e.getMessage());
        }
    }

    // 3. 상세 조회 (이미지 안 나옴)
    @GetMapping("/{id}")
    public ResponseEntity<?> getPartDetail(@PathVariable Long id) {
        try {
            log.info("상세 정보 요청 ID: {}", id);
            LearningObjectResponse detail = objectDataService.getPartDetail(id);
            
            if (detail == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            log.error("상세 조회 중 에러: ", e);
            return ResponseEntity.internalServerError().body("Server Error: " + e.getMessage());
        }
    }
}