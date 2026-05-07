package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体类
 *
 * 对应原型图底部 tab 与管理后台侧边栏菜单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "menus")
public class Menu extends BaseEntity {

    /**
     * 菜单 key (唯一标识,例如 home/bookshelf/profile)
     */
    @NotBlank
    @Column(name = "menu_key", unique = true, nullable = false, length = 64)
    private String menuKey;

    /**
     * 菜单显示文本
     */
    @NotBlank
    @Column(name = "text", nullable = false, length = 100)
    private String text;

    /**
     * 图标(支持 emoji / icon name / 图片 URL)
     */
    @Column(name = "icon", length = 200)
    private String icon;

    /**
     * 路由路径
     */
    @Column(name = "path", length = 200)
    private String path;

    /**
     * 父菜单 ID (顶级为 null)
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 排序
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * 关联权限 ID (可空,公开菜单)
     */
    @Column(name = "permission_id")
    private Long permissionId;

    /**
     * 菜单类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "menu_type", nullable = false, length = 20)
    private MenuType menuType = MenuType.USER;

    /**
     * 是否可见
     */
    @Column(name = "visible", nullable = false)
    private Boolean visible = true;

    public enum MenuType {
        USER,   // 用户端菜单(底部 tab / 个人中心入口)
        ADMIN   // 管理后台菜单
    }
}
