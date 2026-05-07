package com.ikindle.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数 - 与 ConfigDefinition.configKey 关联的实际值
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "system_configs")
public class SystemConfig extends BaseEntity {

    @Column(name = "config_key", unique = true, nullable = false, length = 100)
    private String configKey;

    @Column(name = "config_value", length = 2000)
    private String configValue;

    @Column(name = "remark", length = 500)
    private String remark;
}
