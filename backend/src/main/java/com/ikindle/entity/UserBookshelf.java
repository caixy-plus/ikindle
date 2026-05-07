package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户书架实体类
 *
 * @author iKindle Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_bookshelves")
public class UserBookshelf extends BaseEntity {

    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 图书
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * 阅读进度 (页码)
     */
    @Column(name = "reading_progress")
    private Integer readingProgress = 0;

    /**
     * 阅读百分比
     */
    @Column(name = "reading_percentage")
    private Double readingPercentage = 0.0;

    /**
     * 最后阅读时间
     */
    @Column(name = "last_read_time")
    private LocalDateTime lastReadTime;

    /**
     * 是否收藏
     */
    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = false;

    /**
     * 收藏时间
     */
    @Column(name = "favorite_time")
    private LocalDateTime favoriteTime;

    /**
     * 阅读时长 (秒)
     */
    @Column(name = "reading_duration")
    private Long readingDuration = 0L;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * 同步状态(对应原型图书架页 tab)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status", nullable = false, length = 20)
    private SyncStatus syncStatus = SyncStatus.PENDING;

    public enum SyncStatus {
        PENDING,           // 等待中
        SYNCING,           // 同步中
        SYNCED,            // 已同步
        AWAITING_RECEIPT,  // 待回执
        COMPLETED,         // 已完成
        FAILED             // 失败
    }
}
