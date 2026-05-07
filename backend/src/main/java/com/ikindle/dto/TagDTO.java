package com.ikindle.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TagDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String color;
    private Integer usageCount;
    private Boolean enabled;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}