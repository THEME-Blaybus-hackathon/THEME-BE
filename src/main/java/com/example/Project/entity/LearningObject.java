package com.example.Project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "learning_objects")
public class LearningObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(length = 1000)
    private String description;

    private String imageUrl;
    private String modelUrl;
    private String assemblyUrl;
    
    @Column(name = "mesh_name", length = 100)
    private String meshName;  // GLB 파일 내부 Mesh 이름 (파일명 기반)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private LearningObjectCategory category;
}