package com.example.Project.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Project.dto.QuizSubmitResponse.QuizResult;
import com.example.Project.entity.WrongAnswerNote;
import com.example.Project.repository.WrongAnswerNoteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 오답 노트 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WrongAnswerNoteService {

    private final WrongAnswerNoteRepository wrongAnswerNoteRepository;

    /**
     * 퀴즈 제출 결과에서 오답만 추출하여 저장
     */
    @Transactional
    public void saveWrongAnswers(String userId, String quizId, String objectName, 
                                  java.util.Map<String, QuizResult> results) {
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("userId is null or empty, skipping wrong answer save");
            return;
        }

        int savedCount = 0;
        for (QuizResult result : results.values()) {
            // 오답만 저장
            if (!result.getIsCorrect()) {
                WrongAnswerNote note = WrongAnswerNote.builder()
                        .userId(userId)
                        .quizId(quizId)
                        .questionId(result.getQuestionId())
                        .question(result.getQuestion())
                        .userAnswer(result.getUserAnswer())
                        .correctAnswer(result.getCorrectAnswer())
                        .explanation(result.getExplanation())
                        .category(extractCategory(result))
                        .objectName(objectName)
                        .createdAt(Instant.now())
                        .reviewed(false)
                        .build();

                wrongAnswerNoteRepository.save(note);
                savedCount++;
            }
        }

        log.info("Saved {} wrong answers for user: {}, quiz: {}", savedCount, userId, quizId);
    }

    /**
     * 특정 사용자의 모든 오답 노트 조회
     */
    public List<WrongAnswerNote> getWrongAnswers(String userId) {
        return wrongAnswerNoteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 특정 사용자의 특정 객체에 대한 오답 노트 조회
     */
    public List<WrongAnswerNote> getWrongAnswersByObject(String userId, String objectName) {
        return wrongAnswerNoteRepository.findByUserIdAndObjectNameOrderByCreatedAtDesc(userId, objectName);
    }

    /**
     * 특정 사용자의 미복습 오답 노트 조회
     */
    public List<WrongAnswerNote> getUnreviewedWrongAnswers(String userId) {
        return wrongAnswerNoteRepository.findByUserIdAndReviewedFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * 오답 노트 복습 완료 처리
     */
    @Transactional
    public void markAsReviewed(Long noteId) {
        WrongAnswerNote note = wrongAnswerNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Wrong answer note not found"));
        
        note.setReviewed(true);
        note.setReviewedAt(Instant.now());
        wrongAnswerNoteRepository.save(note);
        
        log.info("Marked wrong answer note {} as reviewed", noteId);
    }

    /**
     * 오답 노트 삭제
     */
    @Transactional
    public void deleteWrongAnswer(Long noteId) {
        wrongAnswerNoteRepository.deleteById(noteId);
        log.info("Deleted wrong answer note {}", noteId);
    }

    /**
     * QuizResult에서 카테고리 추출 (확장 가능)
     */
    private String extractCategory(QuizResult result) {
        // 추후 QuizResult에 category 필드 추가 가능
        return "일반";
    }
}
