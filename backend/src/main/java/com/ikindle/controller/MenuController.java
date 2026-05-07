package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.MenuDTO;
import com.ikindle.entity.Menu;
import com.ikindle.mapper.MenuDtoMapper;
import com.ikindle.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "菜单管理", description = "用户端 + 管理端菜单")
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final MenuDtoMapper mapper;

    @Operation(summary = "查询菜单树")
    @GetMapping("/tree")
    public ApiResponse<List<MenuDTO>> tree(@RequestParam(defaultValue = "USER") Menu.MenuType type) {
        List<Menu> flat = menuService.findTreeByType(type);
        List<MenuDTO> dtos = flat.stream().map(mapper::toDto).toList();
        return ApiResponse.success(buildTree(dtos));
    }

    @Operation(summary = "获取菜单(管理端)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MenuDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(mapper.toDto(menuService.findByIdOrThrow(id)));
    }

    @Operation(summary = "新增菜单(管理端)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MenuDTO> create(@RequestBody MenuDTO dto) {
        Menu saved = menuService.save(mapper.toEntity(dto));
        return ApiResponse.success(mapper.toDto(saved));
    }

    @Operation(summary = "更新菜单(管理端)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MenuDTO> update(@PathVariable Long id, @RequestBody MenuDTO dto) {
        Menu existing = menuService.findByIdOrThrow(id);
        existing.setMenuKey(dto.getMenuKey());
        existing.setText(dto.getText());
        existing.setIcon(dto.getIcon());
        existing.setPath(dto.getPath());
        existing.setParentId(dto.getParentId());
        existing.setSortOrder(dto.getSortOrder());
        existing.setPermissionId(dto.getPermissionId());
        if (dto.getMenuType() != null) {
            existing.setMenuType(Menu.MenuType.valueOf(dto.getMenuType()));
        }
        existing.setVisible(dto.getVisible() == null ? Boolean.TRUE : dto.getVisible());
        return ApiResponse.success(mapper.toDto(menuService.save(existing)));
    }

    @Operation(summary = "删除菜单(管理端)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        menuService.deleteById(id);
        return ApiResponse.success();
    }

    private List<MenuDTO> buildTree(List<MenuDTO> flat) {
        Map<Long, MenuDTO> idMap = new HashMap<>();
        for (MenuDTO dto : flat) {
            idMap.put(dto.getId(), dto);
        }
        List<MenuDTO> roots = new ArrayList<>();
        for (MenuDTO dto : flat) {
            if (dto.getParentId() == null) {
                roots.add(dto);
            } else {
                MenuDTO parent = idMap.get(dto.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                } else {
                    roots.add(dto);
                }
            }
        }
        return roots;
    }
}
