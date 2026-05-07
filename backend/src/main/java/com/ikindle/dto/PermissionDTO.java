package com.ikindle.dto;

import lombok.Data;

@Data
public class PermissionDTO {
    private Long id;
    private String name;
    private String description;
    private String type;
    private String resource;
    private String method;
}
