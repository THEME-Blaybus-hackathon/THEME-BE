package com.example.Project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Project.dto.MemoRequestDto;
import com.example.Project.dto.MemoResponse;
import com.example.Project.dto.MemoUpdateRequest;
import com.example.Project.entity.Memo;
import com.example.Project.repository.MemoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoService {

    private final MemoRepository memoRepository;

    @Transactional
    public MemoResponse createMemo(MemoRequestDto request) {
        Memo memo = Memo.builder()
                .title(request.getTitle())
                .partName(request.getPartName())
                .content(request.getContent())
                .build();
        
        Memo savedMemo = memoRepository.save(memo);
        return convertToResponse(savedMemo);
    }

    public List<MemoResponse> getMemosByPart(String partName) {
        return memoRepository.findByPartNameOrderByCreatedAtDesc(partName).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemoResponse updateMemo(Long id, MemoUpdateRequest request) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 메모가 없습니다. id=" + id));

        // 기존 정보 유지하며 title과 content 업데이트
        Memo updatedMemo = Memo.builder()
                .id(memo.getId())
                .title(request.getTitle())
                .partName(memo.getPartName())
                .content(request.getContent())
                .createdAt(memo.getCreatedAt())
                .build();

        return convertToResponse(memoRepository.save(updatedMemo));
    }

    @Transactional
    public void deleteMemo(Long id) {
        if (!memoRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 메모가 없습니다. id=" + id);
        }
        memoRepository.deleteById(id);
    }

    private MemoResponse convertToResponse(Memo memo) {
        return MemoResponse.builder()
                .id(memo.getId())
                .title(memo.getTitle())
                .partName(memo.getPartName())
                .content(memo.getContent())
                .createdAt(memo.getCreatedAt())
                .build();
    }
}