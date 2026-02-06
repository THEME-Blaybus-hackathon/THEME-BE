package com.example.Project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.CategoryResponse;
import com.example.Project.dto.LearningObjectResponse;
import com.example.Project.service.ObjectDataService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "3D Model API", description = "3D 엔지니어링 모델 및 부품 정보 API")
@RestController
@RequestMapping("/api/objects")
@RequiredArgsConstructor
@Slf4j
public class LearningObjectController {

    private final ObjectDataService objectDataService;

    @Operation(
            summary = "카테고리 목록 조회",
            description = "사용 가능한 모든 3D 모델 카테고리를 조회합니다."
    )
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        try {
            log.info("📋 카테고리 목록 요청");
            List<String> categories = objectDataService.getAllCategories();
            return ResponseEntity.ok(Map.of(
                "count", categories.size(), 
                "categories", categories
            ));
        } catch (Exception e) {
            log.error("❌ 카테고리 조회 중 에러: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Server Error: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "모델별 부품 목록 조회",
            description = "특정 카테고리(모델)의 모든 부품 정보와 대표 이미지를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<?> getPartsList(@RequestParam String category) {
        try {
            log.info("🔍 모델 검색 요청: {}", category);
            CategoryResponse response = objectDataService.getPartsByCategory(category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ 모델 검색 중 에러: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Server Error: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "부품 상세 정보 조회",
            description = "특정 부품의 상세 정보를 ID로 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getPartDetail(@PathVariable Long id) {
        try {
            log.info("📖 상세 정보 요청 ID: {}", id);
            LearningObjectResponse detail = objectDataService.getPartDetail(id);
            
            if (detail == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            log.error("❌ 상세 조회 중 에러: ", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Server Error: " + e.getMessage()));
        }
    }
}