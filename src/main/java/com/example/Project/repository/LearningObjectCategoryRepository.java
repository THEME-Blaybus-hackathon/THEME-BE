package com.example.Project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project.entity.LearningObjectCategory;

public interface LearningObjectCategoryRepository extends JpaRepository<LearningObjectCategory, String> {
}