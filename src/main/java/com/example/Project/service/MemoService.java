package com.example.Project.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Project.dto.MemoRequest;
import com.example.Project.entity.Memo;
import com.example.Project.repository.MemoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;

    @Transactional
    public void saveMemo(MemoRequest request) {
        Memo memo = Memo.builder()
                .partName(request.getPartName())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        memoRepository.save(memo);
    }

    @Transactional(readOnly = true)
    public String getMemo(String partName) {
        return memoRepository.findTopByPartNameOrderByCreatedAtDesc(partName)
                .map(Memo::getContent)
                .orElse(""); // 없으면 빈 문자열 반환
    }
}