package com.example.Project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.Project.dto.MemoRequestDto;
import com.example.Project.dto.MemoResponse;
import com.example.Project.service.MemoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Memo API", description = "메모 관리 API")
@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    // 현재 로그인한 유저의 이메일 가져오기
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }
        return authentication.getName();
    }

    @Operation(summary = "메모 생성")
    @PostMapping
    public ResponseEntity<MemoResponse> createMemo(@RequestBody MemoRequestDto dto) {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(memoService.createMemo(email, dto));
    }

    @Operation(summary = "부품별 내 메모 조회")
    @GetMapping
    public ResponseEntity<List<MemoResponse>> getMemosByPart(@RequestParam String partName) {
        String email = getCurrentUserEmail();
        // [수정] 메서드 이름과 파라미터 맞춤
        return ResponseEntity.ok(memoService.getMemosByPart(email, partName));
    }

    @Operation(summary = "메모 수정")
    @PutMapping("/{id}")
    public ResponseEntity<MemoResponse> updateMemo(@PathVariable Long id, @RequestBody MemoRequestDto dto) {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(memoService.updateMemo(email, id, dto));
    }

    @Operation(summary = "메모 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        memoService.deleteMemo(email, id);
        return ResponseEntity.noContent().build();
    }
}