package com.example.Project.service;

import com.example.Project.dto.*;
import com.example.Project.entity.*;
import com.example.Project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObjectDataService {

    private final LearningObjectCategoryRepository categoryRepository;
    private final LearningObjectRepository learningObjectRepository;

    public List<String> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(LearningObjectCategory::getId)
                .collect(Collectors.toList());
    }

    public CategoryResponse getPartsByCategory(String categoryId) {
        LearningObjectCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category: " + categoryId));

        List<PartSummaryResponse> parts = learningObjectRepository.findByCategoryId(categoryId).stream()
                .map(obj -> new PartSummaryResponse(obj.getId(), obj.getName()))
                .collect(Collectors.toList());

        return CategoryResponse.builder()
                .categoryName(category.getId())
                .mainImageUrl(category.getMainImageUrl())
                .parts(parts)
                .build();
    }

    public LearningObjectResponse getPartDetail(Long id) {
        LearningObject obj = learningObjectRepository.findById(id).orElse(null);
        if (obj == null) return null;

        return LearningObjectResponse.builder()
                .id(obj.getId())
                .name(obj.getName())
                .description(obj.getDescription())
                .imageUrl(obj.getImageUrl())
                .modelUrl(obj.getModelUrl())
                .assemblyUrl(obj.getAssemblyUrl())
                .category(obj.getCategory().getId())
                .build();
    }
}