package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * 图书实体类
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Entity
@Table(name = "books")
@Data
@EqualsAndHashCode(callSuper = true)
public class Book extends BaseEntity {

    /**
     * 图书标题
     */
    @NotBlank(message = "图书标题不能为空")
    @Size(max = 200, message = "图书标题长度不能超过200个字符")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 图书副标题
     */
    @Size(max = 200, message = "图书副标题长度不能超过200个字符")
    @Column(name = "subtitle", length = 200)
    private String subtitle;

    /**
     * 作者
     */
    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者长度不能超过100个字符")
    @Column(name = "author", nullable = false, length = 100)
    private String author;

    /**
     * 图书简介
     */
    @Size(max = 2000, message = "图书简介长度不能超过2000个字符")
    @Column(name = "description", length = 2000)
    private String description;

    /**
     * 图书封面图片URL
     */
    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    /**
     * 图书文件URL
     */
    @Column(name = "file_url", length = 500)
    private String fileUrl;

    /**
     * 文件大小 (字节)
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 文件格式 (PDF, EPUB, MOBI等)
     */
    @Size(max = 20, message = "文件格式长度不能超过20个字符")
    @Column(name = "file_format", length = 20)
    private String fileFormat;

    /**
     * 页数
     */
    @Column(name = "page_count")
    private Integer pageCount;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 原价
     */
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    /**
     * 评级 (1-5星)
     */
    @Column(name = "rating")
    private Double rating = 0.0;

    /**
     * 累计销量
     */
    @Column(name = "sales_count")
    private Integer salesCount = 0;

    /**
     * 库存数量
     */
    @Column(name = "stock_count")
    private Integer stockCount = 0;

    /**
     * 上架状态
     */
    @Column(name = "published", nullable = false)
    private Boolean published = false;

    /**
     * 图书分类
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * 图书标签
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_tags",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // Getters and Setters
} 