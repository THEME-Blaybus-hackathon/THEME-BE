package com.example.Project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project.entity.Memo;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findByPartNameOrderByCreatedAtDesc(String partName);
}