package com.ikindle.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SyncTaskDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private String bookCoverUrl;
    private Long orderItemId;
    private String targetEmail;
    private String status;
    private Integer retryCount;
    private Integer maxRetry;
    private String errorMsg;
    private LocalDateTime syncedTime;
    private LocalDateTime createdTime;
}
