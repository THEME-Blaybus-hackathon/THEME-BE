package com.example.Project.repository;

import com.example.Project.entity.LearningObject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LearningObjectRepository extends JpaRepository<LearningObject, Long> {
    List<LearningObject> findByCategoryId(String categoryId);
}