package com.ikindle.dto;

import lombok.Data;

@Data
public class SystemConfigDTO {
    private Long id;
    private String configKey;
    private String configValue;
    private String remark;
    private String name;
    private String valueType;
    private String description;
}
