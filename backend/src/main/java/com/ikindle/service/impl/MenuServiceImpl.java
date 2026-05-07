package com.ikindle.service.impl;

import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.entity.Menu;
import com.ikindle.repository.MenuRepository;
import com.ikindle.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuServiceImpl extends BaseServiceImpl<Menu, Long> implements MenuService {

    private final MenuRepository menuRepository;

    public MenuServiceImpl(MenuRepository menuRepository) {
        super(menuRepository);
        this.menuRepository = menuRepository;
    }

    @Override
    public List<Menu> findTreeByType(Menu.MenuType menuType) {
        return menuRepository.findByMenuTypeAndVisibleTrueOrderBySortOrderAsc(menuType);
    }

    @Override
    public Menu findByMenuKey(String menuKey) {
        return menuRepository.findByMenuKey(menuKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "菜单不存在: " + menuKey));
    }

    @Override
    public List<Menu> findByParentId(Long parentId) {
        return menuRepository.findByParentIdOrderBySortOrderAsc(parentId);
    }
}
