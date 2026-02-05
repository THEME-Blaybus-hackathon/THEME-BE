package com.example.Project.service;

import java.time.Instant;
import java.time.LocalDateTime;
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

import com.example.Project.entity.QuizAnswer;
import com.example.Project.dto.ChatMessage;
import com.example.Project.dto.QuizGenerateRequest;
import com.example.Project.dto.QuizGenerateResponse;
import com.example.Project.dto.QuizQuestion;
import com.example.Project.dto.QuizSubmitRequest;
import com.example.Project.dto.QuizSubmitResponse;
import com.example.Project.dto.QuizSubmitResponse.QuizResult;
import com.example.Project.entity.QuizRecord;
import com.example.Project.repository.QuizAnswerRepository;
import com.example.Project.repository.QuizRecordRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizService {

    private final OpenAiService openAiService;
    private final PromptService promptService;
    private final QuizRecordRepository quizRecordRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final ChatContextService chatContextService;
    private WrongAnswerNoteService wrongAnswerNoteService;

    // 메모리 내에서 퀴즈 세션 관리
    private final Map<String, List<QuizQuestion>> quizSessions = new ConcurrentHashMap<>();
    private final Map<String, String> quizObjectNames = new ConcurrentHashMap<>();
    private final Map<String, String> quizSessionIds = new ConcurrentHashMap<>();

    public QuizService(OpenAiService openAiService, 
                       PromptService promptService,
                       QuizRecordRepository quizRecordRepository, 
                       QuizAnswerRepository quizAnswerRepository, 
                       ChatContextService chatContextService) {
        this.openAiService = openAiService;
        this.promptService = promptService;
        this.quizRecordRepository = quizRecordRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.chatContextService = chatContextService;
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setWrongAnswerNoteService(WrongAnswerNoteService wrongAnswerNoteService) {
        this.wrongAnswerNoteService = wrongAnswerNoteService;
    }

    // 1. 대화 기반 퀴즈 생성
    public QuizGenerateResponse generateQuizFromChat(String sessionId, String objectName, Integer questionCount) {
        log.info("Generating quiz from chat | session: {} | object: {}", sessionId, objectName);

        if (questionCount == null) {
            questionCount = 3;
        }

        String quizId = UUID.randomUUID().toString();
        List<ChatMessage> chatHistory = chatContextService.getHistory(sessionId, objectName);

        if (chatHistory.isEmpty()) {
            throw new RuntimeException("No chat history. Please chat with AI first.");
        }

        String quizPrompt = buildQuizPromptFromChat(objectName, chatHistory, questionCount);
        List<ChatMessage> messages = List.of(ChatMessage.user(quizPrompt));
        String aiResponse = openAiService.sendChatCompletionForQuiz(messages);

        List<QuizQuestion> questions = parseQuizResponse(aiResponse, questionCount);
        
        // 서버 메모리에 퀴즈 정보 저장
        quizSessions.put(quizId, questions);
        quizObjectNames.put(quizId, objectName);
        quizSessionIds.put(quizId, sessionId);

        // 퀴즈 기록(전체) 초기 저장
        QuizRecord record = QuizRecord.builder()
                .quizId(quizId)
                .objectName(objectName)
                .selectedPart("chat_based")
                .totalQuestions(questions.size())
                .correctAnswers(0)
                .score(0.0)
                .grade(null)
                .createdAt(Instant.now())
                .build();
        quizRecordRepository.save(record);

        return QuizGenerateResponse.fromQuestionsWithoutAnswers(quizId, objectName, "chat_based", questions);
    }

    // 2. 일반 퀴즈 생성
    public QuizGenerateResponse generateQuiz(QuizGenerateRequest request) {
        log.info("Generating quiz | object: {} | part: {}", request.getObjectName(), request.getSelectedPart());

        String quizId = UUID.randomUUID().toString();
        String quizPrompt = buildQuizPrompt(request);
        List<ChatMessage> messages = List.of(ChatMessage.user(quizPrompt));
        String aiResponse = openAiService.sendChatCompletionForQuiz(messages);

        List<QuizQuestion> questions = parseQuizResponse(aiResponse, request.getQuestionCount());
        
        quizSessions.put(quizId, questions);
        quizObjectNames.put(quizId, request.getObjectName());
        // 일반 퀴즈는 특정 채팅 세션과 연관이 없을 수 있으므로 "general" 등으로 저장하거나 null 처리
        quizSessionIds.put(quizId, "general_quiz_session"); 

        QuizRecord record = QuizRecord.builder()
                .quizId(quizId)
                .objectName(request.getObjectName())
                .selectedPart(request.getSelectedPart())
                .totalQuestions(questions.size())
                .correctAnswers(0)
                .score(0.0)
                .grade(null)
                .createdAt(Instant.now())
                .build();
        quizRecordRepository.save(record);

        return QuizGenerateResponse.fromQuestionsWithoutAnswers(quizId, request.getObjectName(), request.getSelectedPart(), questions);
    }

    // 3. 퀴즈 제출 및 채점 (여기가 핵심 수정 부분)
    @Transactional
    public QuizSubmitResponse submitQuiz(QuizSubmitRequest request) {
        List<QuizQuestion> questions = quizSessions.get(request.getQuizId());
        if (questions == null) {
            throw new RuntimeException("Quiz session not found");
        }

        Map<String, QuizResult> results = new HashMap<>();
        int correctCount = 0;

        // 저장해둔 메타 데이터 가져오기
        String objectName = quizObjectNames.getOrDefault(request.getQuizId(), "Unknown");
        String sessionId = quizSessionIds.getOrDefault(request.getQuizId(), "Unknown");

        for (QuizQuestion q : questions) {
            String userAnswer = request.getAnswers().get(q.getId());  // "O" 또는 "X"
            if (userAnswer == null) userAnswer = ""; // null 방지

            boolean isCorrect = userAnswer.equalsIgnoreCase(q.getCorrectAnswer());
            if (isCorrect) {
                correctCount++;
            }

            // 결과 맵에 추가
            results.put(q.getId(), QuizResult.builder()
                    .questionId(q.getId())
                    .question(q.getQuestion())
                    .isCorrect(isCorrect)
                    .userAnswer(userAnswer)
                    .correctAnswer(q.getCorrectAnswer())
                    .explanation(q.getExplanation())
                    .build());

            // ▼▼▼ [추가] PDF 리포트용 개별 답안 DB 저장 ▼▼▼
            try {
                QuizAnswer answerEntity = QuizAnswer.builder()
                        .sessionId(sessionId)      // 세션 ID (PDF 매칭용)
                        .objectName(objectName)    // 부품명
                        .question(q.getQuestion())
                        .userAnswer(userAnswer)
                        .correctAnswer(q.getCorrectAnswer())
                        .isCorrect(isCorrect)
                        .explanation(q.getExplanation())
                        .createdAt(LocalDateTime.now())
                        .build();
                
                quizAnswerRepository.save(answerEntity);
            } catch (Exception e) {
                log.error("Failed to save individual quiz answer for PDF: {}", e.getMessage());
                // 전체 채점 로직이 멈추지 않도록 예외는 로그만 찍고 넘어감
            }
        }

        double score = correctCount * 10.0;
        String grade = null;

        // 전체 기록 업데이트
        QuizRecord record = quizRecordRepository.findByQuizId(request.getQuizId()).orElseThrow();
        record.setUserId(request.getUserId());
        record.setCorrectAnswers(correctCount);
        record.setScore(score);
        record.setGrade(grade);
        record.setSubmittedAt(Instant.now());
        quizRecordRepository.save(record);

        // 오답 노트 서비스 호출 (옵션)
        if (wrongAnswerNoteService != null
                && request.getUserId() != null
                && !request.getUserId().trim().isEmpty()) {
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
                        .correctAnswer((String) jsonQ.get("correctAnswer")) 
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
                    .correctAnswer(m.group(2).trim()) 
                    .explanation(m.group(3).trim())
                    .category(m.group(4).trim())
                    .build());
        }

        return questions;
    }
}