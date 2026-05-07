package com.ikindle.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuDTO {
    private Long id;
    private String menuKey;
    private String text;
    private String icon;
    private String path;
    private Long parentId;
    private Integer sortOrder;
    private Long permissionId;
    private String menuType;
    private Boolean visible;
    private List<MenuDTO> children = new ArrayList<>();
}
