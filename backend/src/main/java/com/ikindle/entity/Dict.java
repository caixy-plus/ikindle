package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典实体类
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Entity
@Table(name = "dict")
@Data
@EqualsAndHashCode(callSuper = true)
public class Dict extends BaseEntity {

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 50, message = "字典类型长度不能超过50个字符")
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /**
     * 字典标签
     */
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    @Column(name = "label", nullable = false, length = 100)
    private String label;

    /**
     * 字典值
     */
    @NotBlank(message = "字典值不能为空")
    @Size(max = 100, message = "字典值长度不能超过100个字符")
    @Column(name = "value", nullable = false, length = 100)
    private String value;

    /**
     * 描述
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 排序
     */
    @Column(name = "sort", nullable = false)
    private Integer sort = 0;

    /**
     * 是否启用
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

}