package com.example.Project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project.entity.Memo;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    Optional<Memo> findTopByPartNameOrderByCreatedAtDesc(String partName);
}