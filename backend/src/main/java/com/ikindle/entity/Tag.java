package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图书标签实体类
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    /**
     * 标签编码
     */
    @NotBlank(message = "标签编码不能为空")
    @Size(max = 20, message = "标签编码长度不能超过20个字符")
    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    /**
     * 标签描述
     */
    @Size(max = 200, message = "标签描述长度不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 标签颜色
     */
    @Size(max = 20, message = "标签颜色长度不能超过20个字符")
    @Column(name = "color", length = 20)
    private String color;

    /**
     * 使用次数
     */
    @Column(name = "usage_count")
    private Integer usageCount = 0;

    /**
     * 是否启用
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
} 