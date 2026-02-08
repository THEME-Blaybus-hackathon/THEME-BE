package com.example.Project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatSession Entity
 * 사용자별 AI 대화 세션을 관리
 */
@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * UUID 형식의 세션 ID (백엔드에서 생성)
     */
    @Column(name = "session_id", nullable = false, unique = true, length = 36)
    private String sessionId;

    /**
     * 세션을 소유한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 학습 중인 객체 이름 (예: "suspension", "robot_arm", "drone")
     */
    @Column(name = "object_name", nullable = false, length = 100)
    private String objectName;

    /**
     * 세션 생성 시간
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 마지막 대화 시간 (업데이트됨)
     */
    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    /**
     * 세션의 모든 대화 메시지
     */
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastActivityAt = LocalDateTime.now();
    }

    /**
     * 세션 활동 시간 업데이트
     */
    public void updateActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }
}
