package com.example.Project.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.MemoRequest;
import com.example.Project.service.MemoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    // 저장
    @PostMapping
    public ResponseEntity<?> save(@RequestBody MemoRequest request) {
        memoService.saveMemo(request);
        return ResponseEntity.ok(Map.of("message", "저장 완료"));
    }

    // 조회
    @GetMapping("/{partName}")
    public ResponseEntity<?> get(@PathVariable String partName) {
        String content = memoService.getMemo(partName);
        return ResponseEntity.ok(Map.of("content", content));
    }
}