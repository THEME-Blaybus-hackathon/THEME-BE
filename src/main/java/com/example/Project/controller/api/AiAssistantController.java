package com.example.Project.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Project.dto.AiChatRequest;
import com.example.Project.dto.AiChatResponse;
import com.example.Project.dto.ApiResponse;
import com.example.Project.dto.ChatHistoryResponse;
import com.example.Project.entity.User;
import com.example.Project.repository.UserRepository;
import com.example.Project.service.AiChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "AI Assistant API", description = "Context-aware AI assistant with session management")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiAssistantController {

    private final AiChatService aiChatService;
    private final UserRepository userRepository;

    // ========== 새로운 DB 기반 API ==========

    @Operation(
        summary = "AI 채팅", 
        description = """
            세션 기반 AI 채팅 API
            
            **새 대화 시작:**
            - sessionId를 보내지 않으면 자동으로 새 세션 생성
            - 세션 ID 형식: yyyyMMdd-NNN (예: 20260208-001)
            
            **기존 대화 이어가기:**
            - 이전에 받은 sessionId를 포함해서 요청
            
            **필수 파라미터:**
            - message: 사용자 질문 (예: "드론의 메인 프레임은 무엇인가요?")
            - objectName: 학습 객체명 (drone, robot_arm, robot_gripper, suspension 등)
            
            **선택 파라미터:**
            - sessionId: 기존 세션 ID (없으면 새 세션)
            - selectedPart: 선택한 부품명 (3D 모델에서 클릭한 부품)
            
            **보안:**
            - JWT 토큰 필수 (Authorization: Bearer {token})
            - 다른 사용자의 세션 접근 불가
            
            **히스토리 관리:**
            - 최근 20개 대화만 OpenAI에 전달 (토큰 절약)
            - 전체 대화는 DB에 저장
            """
    )
    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AiChatRequest request) {
        try {
            // UserDetails에서 이메일 추출
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AiChatResponse response = aiChatService.processChat(user, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("AI Chat Error | user: {} | error: {}", userDetails.getUsername(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(AiChatResponse.of(null, "Error: " + e.getMessage(), false, null));
        }
    }

    @Operation(
        summary = "대화 히스토리 조회", 
        description = """
            특정 세션의 전체 대화 내역을 조회합니다.
            
            **사용 예시:**
            - 이전 대화 내용을 다시 확인할 때
            - 학습 진도를 추적할 때
            
            **응답 포함 정보:**
            - sessionId: 세션 ID
            - objectName: 학습 객체명
            - messages: 전체 대화 내역 (USER/ASSISTANT)
            
            **보안:**
            - 본인의 세션만 조회 가능
            """
    )
    @GetMapping("/chat/history")
    public ResponseEntity<ChatHistoryResponse> getChatHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String sessionId) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            ChatHistoryResponse response = aiChatService.getChatHistory(sessionId, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Get History Error | user: {} | sessionId: {} | error: {}", 
                    userDetails.getUsername(), sessionId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
        summary = "세션 삭제", 
        description = """
            특정 세션과 모든 대화 내역을 삭제합니다.
            
            **삭제되는 데이터:**
            - 세션 정보
            - 해당 세션의 모든 메시지
            
            **주의:**
            - 삭제 후 복구 불가능
            - 본인의 세션만 삭제 가능
            """
    )
    @DeleteMapping("/chat/session")
    public ResponseEntity<ApiResponse<String>> deleteChatSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String sessionId) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            aiChatService.deleteSession(sessionId, user);
            return ResponseEntity.ok(ApiResponse.success("Deleted", "Session deleted successfully"));
        } catch (Exception e) {
            log.error("Delete Session Error | user: {} | sessionId: {} | error: {}", 
                    userDetails.getUsername(), sessionId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed", "Failed to delete session: " + e.getMessage()));
        }
    }
}