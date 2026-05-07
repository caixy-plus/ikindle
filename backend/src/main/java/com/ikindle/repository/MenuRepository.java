package com.ikindle.repository;

import com.ikindle.entity.Menu;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends BaseRepository<Menu, Long> {

    Optional<Menu> findByMenuKey(String menuKey);

    boolean existsByMenuKey(String menuKey);

    List<Menu> findByMenuTypeAndVisibleTrueOrderBySortOrderAsc(Menu.MenuType menuType);

    List<Menu> findByParentIdOrderBySortOrderAsc(Long parentId);
}
