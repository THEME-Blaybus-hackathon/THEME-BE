package com.example.Project.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Project.dto.ChatMessage;
import com.example.Project.dto.QuizGenerateRequest;
import com.example.Project.dto.QuizGenerateResponse;
import com.example.Project.dto.QuizQuestion;
import com.example.Project.dto.QuizSubmitRequest;
import com.example.Project.dto.QuizSubmitResponse;
import com.example.Project.dto.QuizSubmitResponse.QuizResult;
import com.example.Project.entity.QuizRecord;
import com.example.Project.repository.QuizRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizService {

    private final OpenAiService openAiService;
    private final PromptService promptService;
    private final QuizRecordRepository quizRecordRepository;
    private final ChatContextService chatContextService;
    private WrongAnswerNoteService wrongAnswerNoteService;  // Setter 주입으로 변경

    private final Map<String, List<QuizQuestion>> quizSessions = new ConcurrentHashMap<>();
    private final Map<String, String> quizObjectNames = new ConcurrentHashMap<>();  // quizId -> objectName

    // 생성자
    public QuizService(OpenAiService openAiService, PromptService promptService, 
                      QuizRecordRepository quizRecordRepository, ChatContextService chatContextService) {
        this.openAiService = openAiService;
        this.promptService = promptService;
        this.quizRecordRepository = quizRecordRepository;
        this.chatContextService = chatContextService;
    }

    // WrongAnswerNoteService Setter 주입 (순환 의존성 방지)
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setWrongAnswerNoteService(WrongAnswerNoteService wrongAnswerNoteService) {
        this.wrongAnswerNoteService = wrongAnswerNoteService;
    }

    public QuizGenerateResponse generateQuizFromChat(String sessionId, String objectName, Integer questionCount) {
        log.info("Generating quiz from chat | session: {} | object: {}", sessionId, objectName);

        if (questionCount == null) {
            questionCount = 3;  // 기본 3문제
        }

        String quizId = UUID.randomUUID().toString();
        List<ChatMessage> chatHistory = chatContextService.getHistory(sessionId, objectName);

        if (chatHistory.isEmpty()) {
            throw new RuntimeException("No chat history. Please chat with AI first.");
        }

        String quizPrompt = buildQuizPromptFromChat(objectName, chatHistory, questionCount);
        List<ChatMessage> messages = List.of(ChatMessage.user(quizPrompt));
        String aiResponse = openAiService.sendChatCompletionForQuiz(messages);  // OX는 더 짧음

        List<QuizQuestion> questions = parseQuizResponse(aiResponse, questionCount);
        quizSessions.put(quizId, questions);
        quizObjectNames.put(quizId, objectName);  // objectName 저장 (오답 노트용)

        QuizRecord record = QuizRecord.builder()
                .quizId(quizId)
                .objectName(objectName)
                .selectedPart("chat_based")
                .totalQuestions(questions.size())
                .correctAnswers(0)
                .score(0.0)
                .grade(null)  // 등급 제거
                .createdAt(Instant.now())
                .build();
        quizRecordRepository.save(record);

        return QuizGenerateResponse.fromQuestionsWithoutAnswers(quizId, objectName, "chat_based", questions);
    }

    public QuizGenerateResponse generateQuiz(QuizGenerateRequest request) {
        log.info("Generating quiz | object: {} | part: {}", request.getObjectName(), request.getSelectedPart());

        String quizId = UUID.randomUUID().toString();
        String quizPrompt = buildQuizPrompt(request);
        List<ChatMessage> messages = List.of(ChatMessage.user(quizPrompt));
        String aiResponse = openAiService.sendChatCompletionForQuiz(messages);  // OX는 더 짧음

        List<QuizQuestion> questions = parseQuizResponse(aiResponse, request.getQuestionCount());
        quizSessions.put(quizId, questions);
        quizObjectNames.put(quizId, request.getObjectName());  // objectName 저장 (오답 노트용)

        QuizRecord record = QuizRecord.builder()
                .quizId(quizId)
                .objectName(request.getObjectName())
                .selectedPart(request.getSelectedPart())
                .totalQuestions(questions.size())
                .correctAnswers(0)
                .score(0.0)
                .grade(null)  // 등급 제거
                .createdAt(Instant.now())
                .build();
        quizRecordRepository.save(record);

        return QuizGenerateResponse.fromQuestionsWithoutAnswers(quizId, request.getObjectName(), request.getSelectedPart(), questions);
    }

    @Transactional
    public QuizSubmitResponse submitQuiz(QuizSubmitRequest request) {
        List<QuizQuestion> questions = quizSessions.get(request.getQuizId());
        if (questions == null) {
            throw new RuntimeException("Quiz session not found");
        }

        Map<String, QuizResult> results = new HashMap<>();
        int correctCount = 0;

        for (QuizQuestion q : questions) {
            String userAnswer = request.getAnswers().get(q.getId());  // "O" 또는 "X"
            boolean isCorrect = userAnswer != null && userAnswer.equals(q.getCorrectAnswer());
            if (isCorrect) {
                correctCount++;
            }

            results.put(q.getId(), QuizResult.builder()
                    .questionId(q.getId())
                    .question(q.getQuestion())
                    .isCorrect(isCorrect)
                    .userAnswer(userAnswer)
                    .correctAnswer(q.getCorrectAnswer())
                    .explanation(q.getExplanation())
                    .build());
        }

        double score = correctCount * 10.0;  // 문제당 10점, 총 30점 만점
        String grade = null;  // 등급 제거

        QuizRecord record = quizRecordRepository.findByQuizId(request.getQuizId()).orElseThrow();
        record.setUserId(request.getUserId());
        record.setCorrectAnswers(correctCount);
        record.setScore(score);
        record.setGrade(grade);
        record.setSubmittedAt(Instant.now());
        quizRecordRepository.save(record);

        // 오답 노트 저장 (userId가 있고 wrongAnswerNoteService가 주입된 경우에만)
        if (wrongAnswerNoteService != null && 
            request.getUserId() != null && 
            !request.getUserId().trim().isEmpty()) {
            String objectName = quizObjectNames.get(request.getQuizId());
            if (objectName != null) {
                wrongAnswerNoteService.saveWrongAnswers(
                    request.getUserId(), 
                    request.getQuizId(), 
                    objectName, 
                    results
                );
            }
        }

        return QuizSubmitResponse.builder()
                .quizId(request.getQuizId())
                .totalQuestions(questions.size())
                .correctAnswers(correctCount)
                .wrongAnswers(questions.size() - correctCount)
                .score(score)
                .grade(grade)
                .results(results)
                .submittedAt(Instant.now())
                .build();
    }

    public List<QuizRecord> getUserQuizHistory(String userId) {
        return quizRecordRepository.findByUserId(userId);
    }

    public List<QuizRecord> getObjectQuizHistory(String objectName) {
        return quizRecordRepository.findByObjectName(objectName);
    }

    private String buildQuizPromptFromChat(String objectName, List<ChatMessage> chatHistory, int questionCount) {
        String systemPrompt = promptService.getSystemPrompt(objectName);
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an OX quiz generator for engineering education.\\n\\nContext:\\n").append(systemPrompt).append("\\n\\n");
        prompt.append("User conversation:\\n---\\n");

        int startIdx = Math.max(0, chatHistory.size() - 10);
        for (int i = startIdx; i < chatHistory.size(); i++) {
            ChatMessage msg = chatHistory.get(i);
            String role = "user".equals(msg.getRole()) ? "Student" : "AI";
            prompt.append(role).append(": ").append(msg.getContent()).append("\\n");
        }
        prompt.append("---\\n\\n");
        prompt.append("Generate ").append(questionCount).append(" OX quiz questions based on this conversation.\\n\\n");
        prompt.append("IMPORTANT: Focus on discussed topics, concepts, and parts.\\n");
        prompt.append("Each question must be a TRUE/FALSE statement in Korean.\\n\\n");
        prompt.append("CRITICAL: Respond ONLY with JSON array:\\n");
        prompt.append("[{\\\"question\\\":\\\"크랭크축은 왕복운동을 회전운동으로 변환한다.\\\",\\\"correctAnswer\\\":\\\"O\\\",\\\"explanation\\\":\\\"크랭크축은...\\\",\\\"category\\\":\\\"동력전달\\\"}]\\n");

        return prompt.toString();
    }

    private String buildQuizPrompt(QuizGenerateRequest request) {
        String systemPrompt = promptService.getSystemPrompt(request.getObjectName());
        StringBuilder prompt = new StringBuilder();
        prompt.append("OX quiz generator for engineering education.\\n\\nContext:\\n").append(systemPrompt).append("\\n\\n");

        if (request.getSelectedPart() != null) {
            prompt.append("Focus: ").append(request.getSelectedPart()).append("\\n\\n");
        }

        prompt.append("Generate ").append(request.getQuestionCount()).append(" OX quiz questions in Korean.\\n");
        prompt.append("Each question must be a TRUE/FALSE statement.\\n\\n");
        if (request.getDifficulty() != null) {
            prompt.append("Difficulty: ").append(request.getDifficulty()).append("\\n\\n");
        }

        prompt.append("CRITICAL: JSON array only:\\n");
        prompt.append("[{\\\"question\\\":\\\"크랭크축은 왕복운동을 회전운동으로 변환한다.\\\",\\\"correctAnswer\\\":\\\"O\\\",\\\"explanation\\\":\\\"크랭크축은...\\\",\\\"category\\\":\\\"동력전달\\\"}]\\n");

        return prompt.toString();
    }

    private List<QuizQuestion> parseQuizResponse(String aiResponse, int expectedCount) {
        List<QuizQuestion> questions = new ArrayList<>();

        try {
            String jsonContent = aiResponse.trim();
            if (jsonContent.startsWith("```json")) {
                jsonContent = jsonContent.substring(7);
            }
            if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.substring(3);
            }
            if (jsonContent.endsWith("```")) {
                jsonContent = jsonContent.substring(0, jsonContent.length() - 3);
            }
            jsonContent = jsonContent.trim();

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            List<Map<String, Object>> jsonQuestions = mapper.readValue(jsonContent,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {
            });

            int num = 1;
            for (Map<String, Object> jsonQ : jsonQuestions) {
                if (questions.size() >= expectedCount) {
                    break;
                }

                questions.add(QuizQuestion.builder()
                        .id("q" + num++)
                        .question((String) jsonQ.get("question"))
                        .correctAnswer((String) jsonQ.get("correctAnswer")) // "O" 또는 "X"
                        .explanation((String) jsonQ.get("explanation"))
                        .category((String) jsonQ.get("category"))
                        .build());
            }
        } catch (Exception e) {
            log.error("JSON parsing failed: {}", e.getMessage());
            return parseQuizResponseFallback(aiResponse, expectedCount);
        }

        if (questions.isEmpty()) {
            throw new RuntimeException("Failed to parse quiz response");
        }

        return questions;
    }

    private List<QuizQuestion> parseQuizResponseFallback(String aiResponse, int expectedCount) {
        List<QuizQuestion> questions = new ArrayList<>();
        Pattern p = Pattern.compile(
                "Q\\\\d+:\\\\s*(.+?)\\\\s*ANSWER:\\\\s*([OX])\\\\s*EXPLANATION:\\\\s*(.+?)\\\\s*CATEGORY:\\\\s*(.+?)(?=Q\\\\d+:|$)",
                Pattern.DOTALL);

        Matcher m = p.matcher(aiResponse);
        int num = 1;

        while (m.find() && questions.size() < expectedCount) {
            questions.add(QuizQuestion.builder()
                    .id("q" + num++)
                    .question(m.group(1).trim())
                    .correctAnswer(m.group(2).trim()) // "O" 또는 "X"
                    .explanation(m.group(3).trim())
                    .category(m.group(4).trim())
                    .build());
        }

        return questions;
    }
}
