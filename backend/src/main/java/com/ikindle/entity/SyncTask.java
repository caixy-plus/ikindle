package com.ikindle.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 同步任务 - 将订单中的电子书发送到用户的 Kindle 邮箱
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sync_tasks")
public class SyncTask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "target_email", nullable = false, length = 200)
    private String targetEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "max_retry", nullable = false)
    private Integer maxRetry = 3;

    @Column(name = "error_msg", length = 1000)
    private String errorMsg;

    @Column(name = "synced_time")
    private LocalDateTime syncedTime;

    public enum TaskStatus {
        PENDING, SYNCING, COMPLETED, FAILED, CANCELLED
    }
}
