package com.example.Project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Project.dto.MemoRequestDto;
import com.example.Project.dto.MemoResponse;
import com.example.Project.entity.Memo;
import com.example.Project.entity.User;
import com.example.Project.repository.MemoRepository;
import com.example.Project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    // 이메일로 유저 찾기 (공통 메서드)
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
    }

    // [메모 생성]
    @Transactional
    public MemoResponse createMemo(String email, MemoRequestDto dto) {
        User user = getUserByEmail(email);

        Memo memo = Memo.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .partName(dto.getPartName())
                .user(user)
                .build();
        
        // save 할 때 createdAt이 자동 생성됨
        return new MemoResponse(memoRepository.save(memo));
    }

    // [부품별 메모 조회] (최신순)
    public List<MemoResponse> getMemosByPart(String email, String partName) {
        User user = getUserByEmail(email);
        return memoRepository.findByUserAndPartNameOrderByCreatedAtDesc(user, partName)
                .stream()
                .map(MemoResponse::new)
                .collect(Collectors.toList());
    }

    // [메모 상세 조회]
    public MemoResponse getMemoById(String email, Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메모가 존재하지 않습니다. id=" + memoId));
        
        // 권한 체크
        if (!memo.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 메모만 조회할 수 있습니다.");
        }

        return new MemoResponse(memo);
    }

    // [메모 수정]
    @Transactional
    public MemoResponse updateMemo(String email, Long memoId, MemoRequestDto dto) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모가 없습니다."));
        
        // 권한 체크
        if (!memo.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 메모만 수정 가능합니다.");
        }

        // 내용 변경 (Dirty Checking이 동작하지만, 리턴값에 날짜를 즉시 반영하기 위해 save 권장)
        memo.setTitle(dto.getTitle());
        memo.setContent(dto.getContent());
        // partName도 수정이 필요하다면 아래 주석 해제
        // memo.setPartName(dto.getPartName());
        
        // ★ save를 호출하면 @LastModifiedDate가 갱신된 엔티티가 반환됨
        Memo updatedMemo = memoRepository.save(memo);
        
        return new MemoResponse(updatedMemo);
    }

    // [메모 삭제]
    @Transactional
    public void deleteMemo(String email, Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모가 없습니다."));

        // 권한 체크
        if (!memo.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 메모만 삭제 가능합니다.");
        }

        memoRepository.delete(memo);
    }
}