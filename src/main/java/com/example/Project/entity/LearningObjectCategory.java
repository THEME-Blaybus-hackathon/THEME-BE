package com.example.Project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "learning_object_categories")
public class LearningObjectCategory {
    @Id
    private String id; // v4_engine, drone 등

    private String mainImageUrl;
}