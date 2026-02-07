package com.example.Project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
import com.example.Project.dto.MemoUpdateRequest;
import com.example.Project.service.MemoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Memo API")
@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping
    @Operation(summary = "메모 생성")
    public ResponseEntity<MemoResponse> createMemo(@RequestBody MemoRequestDto request) {
        return ResponseEntity.ok(memoService.createMemo(request));
    }

    @GetMapping
    @Operation(summary = "부품별 메모 조회")
    public ResponseEntity<List<MemoResponse>> getMemos(@RequestParam String partName) {
        return ResponseEntity.ok(memoService.getMemosByPart(partName));
    }

    @PutMapping("/{id}")
    @Operation(summary = "메모 수정")
    public ResponseEntity<MemoResponse> updateMemo(@PathVariable Long id, @RequestBody MemoUpdateRequest request) {
        return ResponseEntity.ok(memoService.updateMemo(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "메모 삭제")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {
        memoService.deleteMemo(id);
        return ResponseEntity.noContent().build();
    }
}