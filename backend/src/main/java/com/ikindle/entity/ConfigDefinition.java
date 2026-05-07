package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 配置定义 - 描述系统参数的元数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "config_definitions")
public class ConfigDefinition extends BaseEntity {

    @NotBlank
    @Column(name = "config_key", unique = true, nullable = false, length = 100)
    private String configKey;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false, length = 20)
    private ValueType valueType = ValueType.STRING;

    @Column(name = "default_value", length = 1000)
    private String defaultValue;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "category", length = 50)
    private String category;

    public enum ValueType {
        STRING, NUMBER, BOOLEAN, JSON
    }
}
