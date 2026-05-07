package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "permissions")
public class Permission extends BaseEntity {

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过50个字符")
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    /**
     * 权限描述
     */
    @Size(max = 200, message = "权限描述长度不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 权限类型 (API, MENU, FUNCTION)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PermissionType type;

    /**
     * 权限资源 (API路径、菜单路径等)
     */
    @Size(max = 200, message = "权限资源长度不能超过200个字符")
    @Column(name = "resource", length = 200)
    private String resource;

    /**
     * 权限方法 (GET, POST, PUT, DELETE等)
     */
    @Size(max = 50, message = "权限方法长度不能超过50个字符")
    @Column(name = "method", length = 50)
    private String method;

    // 权限类型枚举
    public enum PermissionType {
        API,        // API权限
        MENU,       // 菜单权限
        FUNCTION    // 功能权限
    }

} 