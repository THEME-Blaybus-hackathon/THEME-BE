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

@Service
@RequiredArgsConstructor
@Slf4j
public class WrongAnswerNoteService {

    private final WrongAnswerNoteRepository wrongAnswerNoteRepository;

    @Transactional
    public void saveWrongAnswers(String userId, String quizId, String objectName,
            java.util.Map<String, QuizResult> results) {
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("userId is null or empty, skipping wrong answer save");
            return;
        }

        int savedCount = 0;
        for (QuizResult result : results.values()) {
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

    public List<WrongAnswerNote> getWrongAnswers(String userId) {
        return wrongAnswerNoteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<WrongAnswerNote> getWrongAnswersByObject(String userId, String objectName) {
        return wrongAnswerNoteRepository.findByUserIdAndObjectNameOrderByCreatedAtDesc(userId, objectName);
    }

    public List<WrongAnswerNote> getUnreviewedWrongAnswers(String userId) {
        return wrongAnswerNoteRepository.findByUserIdAndReviewedFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsReviewed(Long noteId) {
        WrongAnswerNote note = wrongAnswerNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Wrong answer note not found"));

        note.setReviewed(true);
        note.setReviewedAt(Instant.now());
        wrongAnswerNoteRepository.save(note);

        log.info("Marked wrong answer note {} as reviewed", noteId);
    }

    @Transactional
    public void deleteWrongAnswer(Long noteId) {
        wrongAnswerNoteRepository.deleteById(noteId);
        log.info("Deleted wrong answer note {}", noteId);
    }

    private String extractCategory(QuizResult result) {
        return "일반";
    }
}
