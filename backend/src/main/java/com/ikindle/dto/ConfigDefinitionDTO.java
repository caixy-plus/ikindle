package com.ikindle.dto;

import lombok.Data;

@Data
public class ConfigDefinitionDTO {
    private Long id;
    private String configKey;
    private String name;
    private String valueType;
    private String defaultValue;
    private String description;
    private String category;
}
