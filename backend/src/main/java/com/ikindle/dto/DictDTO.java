package com.ikindle.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典DTO
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Data
public class DictDTO {

    private Long id;

    private String type;

    private String label;

    private String value;

    private String description;

    private Integer sort;

    private Boolean enabled;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}