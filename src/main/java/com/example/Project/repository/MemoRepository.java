package com.example.Project.repository;

import com.example.Project.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findByPartNameOrderByCreatedAtDesc(String partName);
}