package com.example.Project.controller.api;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.entity.WrongAnswerNote;
import com.example.Project.repository.WrongAnswerNoteRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Test Data API", description = "테스트 데이터 생성")
@RestController
@RequestMapping("/api/test-data")
@RequiredArgsConstructor
@Slf4j
public class TestDataController {

    private final WrongAnswerNoteRepository wrongAnswerNoteRepository;

    @Operation(summary = "테스트용 오답 노트 생성", description = "샘플 오답 노트 3개 생성")
    @PostMapping("/wrong-answers/mock")
    public ResponseEntity<String> createMockWrongAnswers() {
        log.info("Creating mock wrong answer notes...");

        // 목 데이터 1
        WrongAnswerNote note1 = WrongAnswerNote.builder()
                .userId("test_user_mock")
                .quizId("mock_quiz_001")
                .questionId("q1")
                .question("크랭크축은 왕복운동을 회전운동으로 변환한다.")
                .userAnswer("X")
                .correctAnswer("O")
                .explanation("크랭크축은 피스톤의 왕복운동을 받아 회전운동으로 변환하는 핵심 부품입니다.")
                .category("동력전달")
                .objectName("v4_engine")
                .createdAt(Instant.now())
                .reviewed(false)
                .build();

        // 목 데이터 2
        WrongAnswerNote note2 = WrongAnswerNote.builder()
                .userId("test_user_mock")
                .quizId("mock_quiz_001")
                .questionId("q2")
                .question("V4 엔진에서 카운터웨이트는 진동을 증가시킨다.")
                .userAnswer("O")
                .correctAnswer("X")
                .explanation("카운터웨이트는 V4 엔진의 불균형을 보정하여 진동을 감소시키는 역할을 합니다.")
                .category("진동/밸런스")
                .objectName("v4_engine")
                .createdAt(Instant.now())
                .reviewed(false)
                .build();

        // 목 데이터 3
        WrongAnswerNote note3 = WrongAnswerNote.builder()
                .userId("test_user_mock")
                .quizId("mock_quiz_002")
                .questionId("q1")
                .question("피스톤링은 연소실의 가스 밀폐를 담당한다.")
                .userAnswer("X")
                .correctAnswer("O")
                .explanation("피스톤링은 연소실의 가스 누설을 방지하고 압축을 유지하는 중요한 역할을 합니다.")
                .category("피스톤/밀폐")
                .objectName("v4_engine")
                .createdAt(Instant.now().minusSeconds(3600)) // 1시간 전
                .reviewed(false)
                .build();

        wrongAnswerNoteRepository.save(note1);
        wrongAnswerNoteRepository.save(note2);
        wrongAnswerNoteRepository.save(note3);

        log.info("Created 3 mock wrong answer notes for user: test_user_mock");

        return ResponseEntity.ok("Successfully created 3 mock wrong answer notes");
    }
}
