package com.ikindle.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * OAuth 会话实体
 * 存储上游平台 token,与本地 User 关联
 * 设计对齐 brain-think 的 UserSession
 */
@Entity
@Table(name = "user_oauth_sessions")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserOAuthSession extends BaseEntity {

    @Column(name = "session_key", nullable = false, unique = true, length = 64)
    private String sessionKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "display_name", length = 255)
    private String displayName;
}
