package com.ikindle.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String icon;
    private Integer sortOrder;
    private Boolean enabled;
    private CategoryDTO parent;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}