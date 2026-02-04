package com.example.Project.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.QuizGenerateResponse;
import com.example.Project.dto.QuizSubmitRequest;
import com.example.Project.dto.QuizSubmitResponse;
import com.example.Project.service.QuizService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Quiz API", description = "퀴즈 생성 및 채점")
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "대화 기반 OX 퀴즈 생성", description = "AI 대화 내용을 분석하여 OX 퀴즈 생성 (기본 3문제)")

    @PostMapping("/generate-from-chat")
    public ResponseEntity<QuizGenerateResponse> generateQuizFromChat(
            @RequestParam String sessionId,
            @RequestParam String objectName,
            @RequestParam(required = false, defaultValue = "3") Integer questionCount) {
        log.info("Quiz From Chat Request | session: {} | object: {} | count: {}",
                sessionId, objectName, questionCount);

        try {
            QuizGenerateResponse response = quizService.generateQuizFromChat(sessionId, objectName, questionCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating quiz from chat", e);
            return ResponseEntity.internalServerError()
                    .body(QuizGenerateResponse.builder().questions(List.of()).build());
        }
    }

    @Operation(summary = "퀴즈 제출 및 채점", description = "답안 제출 및 자동 채점, 오답은 자동으로 오답노트에 저장")
    @PostMapping("/submit")
    public ResponseEntity<QuizSubmitResponse> submitQuiz(@Valid @RequestBody QuizSubmitRequest request) {
        log.info("Quiz Submit Request | quizId: {} | answers: {}",
                request.getQuizId(), request.getAnswers().size());

        try {
            QuizSubmitResponse response = quizService.submitQuiz(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error submitting quiz", e);
            return ResponseEntity.badRequest()
                    .body(QuizSubmitResponse.builder()
                            .totalQuestions(0)
                            .correctAnswers(0)
                            .wrongAnswers(0)
                            .score(0.0)
                            .grade("ERROR")
                            .build());
        }
    }
}
