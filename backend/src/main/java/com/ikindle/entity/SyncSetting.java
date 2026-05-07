package com.ikindle.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 同步设置 - 用户的 Kindle 邮箱与同步偏好
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sync_settings")
public class SyncSetting extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "kindle_email", length = 200)
    private String kindleEmail;

    @Column(name = "auto_sync", nullable = false)
    private Boolean autoSync = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private Priority priority = Priority.NORMAL;

    @Column(name = "preferred_format", length = 10)
    private String preferredFormat;

    public enum Priority {
        LOW, NORMAL, HIGH
    }
}
