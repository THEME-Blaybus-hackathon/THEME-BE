package com.example.Project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "부품별 내 메모 목록 조회")
    @GetMapping
    public ResponseEntity<List<MemoResponse>> getMemosByPart(@RequestParam String partName) {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(memoService.getMemosByPart(email, partName));
    }

    @Operation(summary = "ID로 메모 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<MemoResponse> getMemoById(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(memoService.getMemoById(email, id));
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