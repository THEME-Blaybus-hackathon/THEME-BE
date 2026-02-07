package com.example.Project.service;

import com.example.Project.dto.*;
import com.example.Project.entity.Memo;
import com.example.Project.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoService {

    private final MemoRepository memoRepository;

    @Transactional
    public MemoResponse createMemo(MemoRequest request) {
        Memo memo = Memo.builder()
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

    // [에러 해결 1] 수정 로직
    @Transactional
    public MemoResponse updateMemo(Long id, MemoUpdateRequest request) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 메모가 없습니다. id=" + id));

        // 빌더 패턴으로 업데이트 (엔티티에 @Setter가 없을 경우 가장 안전)
        Memo updatedMemo = Memo.builder()
                .id(memo.getId())
                .partName(memo.getPartName())
                .content(request.getContent())
                .createdAt(memo.getCreatedAt())
                .build();

        return convertToResponse(memoRepository.save(updatedMemo));
    }

    // [에러 해결 2] 삭제 로직 (컨트롤러에서 찾던 놈)
    @Transactional
    public void deleteMemo(Long id) {
        if (!memoRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 메모가 없습니다. id=" + id);
        }
        memoRepository.deleteById(id);
    }

    // [에러 해결 3] DTO 변환 헬퍼 메서드 (서비스 내부에서 사용)
    private MemoResponse convertToResponse(Memo memo) {
        return MemoResponse.builder()
                .id(memo.getId())
                .partName(memo.getPartName())
                .content(memo.getContent())
                .createdAt(memo.getCreatedAt())
                .build();
    }
}