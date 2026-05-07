package com.ikindle.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String subtitle;
    private String author;
    private String description;
    private String coverUrl;
    private String fileUrl;
    private Long fileSize;
    private String fileFormat;
    private Integer pageCount;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Double rating;
    private Integer salesCount;
    private Integer stockCount;
    private Boolean published;
    private CategoryDTO category;
    private Set<TagDTO> tags;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}