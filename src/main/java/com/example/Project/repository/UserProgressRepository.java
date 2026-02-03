package com.example.Project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Project.entity.User;
import com.example.Project.entity.UserProgress;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    // 특정 사용자의 모든 학습 진도 조회 (최근 접근 순)
    List<UserProgress> findByUserOrderByLastAccessedAtDesc(User user);

    // 특정 사용자의 특정 모델 진도 조회
    Optional<UserProgress> findByUserAndObjectName(User user, String objectName);

    // 특정 사용자의 학습 진도 개수
    long countByUser(User user);
}
