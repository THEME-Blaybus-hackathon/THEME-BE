package com.example.Project.controller;

import com.example.Project.dto.*;
import com.example.Project.service.ObjectDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "3D Model API")
@RestController
@RequestMapping("/api/objects")
@RequiredArgsConstructor
public class LearningObjectController {

    private final ObjectDataService objectDataService;

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        List<String> categories = objectDataService.getAllCategories();
        return ResponseEntity.ok(Map.of("categories", categories));
    }

    @GetMapping
    public ResponseEntity<?> getPartsList(@RequestParam String category) {
        return ResponseEntity.ok(objectDataService.getPartsByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPartDetail(@PathVariable Long id) {
        LearningObjectResponse detail = objectDataService.getPartDetail(id);
        return detail != null ? ResponseEntity.ok(detail) : ResponseEntity.notFound().build();
    }
}