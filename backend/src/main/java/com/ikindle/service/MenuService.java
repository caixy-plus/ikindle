package com.ikindle.service;

import com.ikindle.entity.Menu;

import java.util.List;

/**
 * 菜单 Service
 */
public interface MenuService extends BaseService<Menu, Long> {

    List<Menu> findTreeByType(Menu.MenuType menuType);

    Menu findByMenuKey(String menuKey);

    List<Menu> findByParentId(Long parentId);
}
