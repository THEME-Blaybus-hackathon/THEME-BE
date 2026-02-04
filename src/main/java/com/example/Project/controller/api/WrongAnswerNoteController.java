package com.example.Project.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.entity.WrongAnswerNote;
import com.example.Project.service.WrongAnswerNoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 오답 노트 API 컨트롤러
 */
@Tag(name = "Wrong Answer Note API", description = "오답 노트 관리 API")
@RestController
@RequestMapping("/api/wrong-answers")
@RequiredArgsConstructor
@Slf4j
public class WrongAnswerNoteController {

    private final WrongAnswerNoteService wrongAnswerNoteService;

    @Operation(
            summary = "오답 노트 전체 조회",
            description = "특정 사용자의 모든 오답 노트를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<WrongAnswerNote>> getWrongAnswers(
            @RequestParam String userId
    ) {
        log.info("Get wrong answers | userId: {}", userId);
        List<WrongAnswerNote> notes = wrongAnswerNoteService.getWrongAnswers(userId);
        return ResponseEntity.ok(notes);
    }

    @Operation(
            summary = "객체별 오답 노트 조회",
            description = "특정 사용자의 특정 3D 객체에 대한 오답 노트를 조회합니다."
    )
    @GetMapping("/object/{objectName}")
    public ResponseEntity<List<WrongAnswerNote>> getWrongAnswersByObject(
            @RequestParam String userId,
            @PathVariable String objectName
    ) {
        log.info("Get wrong answers by object | userId: {}, object: {}", userId, objectName);
        List<WrongAnswerNote> notes = wrongAnswerNoteService.getWrongAnswersByObject(userId, objectName);
        return ResponseEntity.ok(notes);
    }

    @Operation(
            summary = "미복습 오답 노트 조회",
            description = "특정 사용자의 아직 복습하지 않은 오답 노트를 조회합니다."
    )
    @GetMapping("/unreviewed")
    public ResponseEntity<List<WrongAnswerNote>> getUnreviewedWrongAnswers(
            @RequestParam String userId
    ) {
        log.info("Get unreviewed wrong answers | userId: {}", userId);
        List<WrongAnswerNote> notes = wrongAnswerNoteService.getUnreviewedWrongAnswers(userId);
        return ResponseEntity.ok(notes);
    }

    @Operation(
            summary = "오답 노트 복습 완료 처리",
            description = "특정 오답 노트를 복습 완료 상태로 변경합니다."
    )
    @PutMapping("/{noteId}/review")
    public ResponseEntity<Void> markAsReviewed(
            @PathVariable Long noteId
    ) {
        log.info("Mark as reviewed | noteId: {}", noteId);
        wrongAnswerNoteService.markAsReviewed(noteId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "오답 노트 삭제",
            description = "특정 오답 노트를 삭제합니다."
    )
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteWrongAnswer(
            @PathVariable Long noteId
    ) {
        log.info("Delete wrong answer | noteId: {}", noteId);
        wrongAnswerNoteService.deleteWrongAnswer(noteId);
        return ResponseEntity.ok().build();
    }
}
