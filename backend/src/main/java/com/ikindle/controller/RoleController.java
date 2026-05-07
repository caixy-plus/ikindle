package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.RoleDTO;
import com.ikindle.entity.Role;
import com.ikindle.mapper.RoleDtoMapper;
import com.ikindle.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理", description = "角色 CRUD + 权限分配")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;
    private final RoleDtoMapper mapper;

    @Operation(summary = "角色列表")
    @GetMapping
    public ApiResponse<List<RoleDTO>> list() {
        return ApiResponse.success(roleService.findAll().stream().map(mapper::toDto).toList());
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public ApiResponse<RoleDTO> create(@RequestBody RoleDTO dto) {
        Role entity = mapper.toEntity(dto);
        return ApiResponse.success(mapper.toDto(roleService.save(entity)));
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    public ApiResponse<RoleDTO> update(@PathVariable Long id, @RequestBody RoleDTO dto) {
        Role existing = roleService.findByIdOrThrow(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        return ApiResponse.success(mapper.toDto(roleService.save(existing)));
    }

    @Operation(summary = "分配权限")
    @PostMapping("/{id}/permissions")
    public ApiResponse<RoleDTO> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        return ApiResponse.success(mapper.toDto(roleService.assignPermissions(id, permissionIds)));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.deleteById(id);
        return ApiResponse.success();
    }
}
