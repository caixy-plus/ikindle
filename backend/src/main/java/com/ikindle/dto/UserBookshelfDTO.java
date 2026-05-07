package com.ikindle.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBookshelfDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCoverUrl;
    private String bookDescription;
    private Integer readingProgress;
    private Double readingPercentage;
    private LocalDateTime lastReadTime;
    private Boolean isFavorite;
    private LocalDateTime favoriteTime;
    private Long readingDuration;
    private String syncStatus;
    private String remark;
    private LocalDateTime createdTime;
}
