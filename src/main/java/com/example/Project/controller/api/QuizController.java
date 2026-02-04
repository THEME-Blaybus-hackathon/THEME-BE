package com.example.Project.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.ApiResponse;
import com.example.Project.dto.QuizGenerateRequest;
import com.example.Project.dto.QuizGenerateResponse;
import com.example.Project.dto.QuizSubmitRequest;
import com.example.Project.dto.QuizSubmitResponse;
import com.example.Project.entity.QuizRecord;
import com.example.Project.service.QuizService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Quiz API", description = "AI-powered quiz generation and grading for 3D model learning")
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;

    @Operation(
            summary = "Generate quiz",
            description = "Generate multiple-choice quiz questions using AI. "
            + "Supports: v4_engine, suspension, robot_gripper, robot_arm, machine_vice, leaf_spring, drone. "
            + "Optional selectedPart parameter for part-specific quizzes."
    )
    @PostMapping("/generate")
    public ResponseEntity<QuizGenerateResponse> generateQuiz(@Valid @RequestBody QuizGenerateRequest request) {
        log.info("Quiz Generate Request | object: {} | part: {} | count: {}",
                request.getObjectName(), request.getSelectedPart(), request.getQuestionCount());

        try {
            QuizGenerateResponse response = quizService.generateQuiz(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating quiz", e);
            return ResponseEntity.internalServerError()
                    .body(QuizGenerateResponse.builder()
                            .questions(List.of())
                            .build());
        }
    }

    @Operation(
            summary = "Generate quiz from chat history",
            description = "Generate quiz based on previous AI conversations. "
            + "Requires existing chat history in the session."
    )
    @PostMapping("/generate-from-chat")
    public ResponseEntity<QuizGenerateResponse> generateQuizFromChat(
            @RequestParam String sessionId,
            @RequestParam String objectName,
            @RequestParam(required = false, defaultValue = "5") Integer questionCount) {
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

    @Operation(
            summary = "Submit quiz answers",
            description = "Submit quiz answers for grading. Returns score, grade, and detailed results."
    )
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

    @Operation(
            summary = "Get user quiz history",
            description = "Get all quiz records for a specific user"
    )
    @GetMapping("/history/user")
    public ResponseEntity<ApiResponse<List<QuizRecord>>> getUserQuizHistory(@RequestParam String userId) {
        log.info("Get User Quiz History | userId: {}", userId);

        List<QuizRecord> history = quizService.getUserQuizHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(history, "User quiz history retrieved"));
    }

    @Operation(
            summary = "Get object quiz history",
            description = "Get all quiz records for a specific 3D model"
    )
    @GetMapping("/history/object")
    public ResponseEntity<ApiResponse<List<QuizRecord>>> getObjectQuizHistory(@RequestParam String objectName) {
        log.info("Get Object Quiz History | objectName: {}", objectName);

        List<QuizRecord> history = quizService.getObjectQuizHistory(objectName);
        return ResponseEntity.ok(ApiResponse.success(history, "Object quiz history retrieved"));
    }
}
