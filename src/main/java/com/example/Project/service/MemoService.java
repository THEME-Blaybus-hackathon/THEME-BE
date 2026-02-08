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

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
    }

    @Transactional
    public MemoResponse createMemo(String email, MemoRequestDto dto) {
        User user = getUserByEmail(email);

        Memo memo = Memo.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .partName(dto.getPartName())
                .user(user) // ★ 여기서 넣은 유저가 이제 DB user_id로 잘 들어갈 겁니다.
                .build();

        return new MemoResponse(memoRepository.save(memo));
    }

    public List<MemoResponse> getMemosByPart(String email, String partName) {
        User user = getUserByEmail(email);
        // ★ 최신순 정렬 메서드로 교체
        return memoRepository.findByUserAndPartNameOrderByCreatedAtDesc(user, partName)
                .stream()
                .map(MemoResponse::new)
                .collect(Collectors.toList());
    }

    public MemoResponse getMemoById(String email, Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메모가 존재하지 않습니다. id=" + memoId));
        
        if (!memo.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 메모만 조회할 수 있습니다.");
        }

        return new MemoResponse(memo);
    }

    @Transactional
    public MemoResponse updateMemo(String email, Long memoId, MemoRequestDto dto) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모가 없습니다."));
        
        if (!memo.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 메모만 수정 가능합니다.");
        }

        memo.setTitle(dto.getTitle());
        memo.setContent(dto.getContent());
        
        return new MemoResponse(memo);
    }

    @Transactional
    public void deleteMemo(String email, Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("메모가 없습니다."));

        if (!memo.getUser().getEmail().equals(email)) {
            throw new IllegalStateException("본인의 메모만 삭제 가능합니다.");
        }

        memoRepository.delete(memo);
    }
}