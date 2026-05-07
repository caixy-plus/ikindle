package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.PermissionDTO;
import com.ikindle.entity.Permission;
import com.ikindle.mapper.PermissionDtoMapper;
import com.ikindle.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "权限管理", description = "权限 CRUD")
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionDtoMapper mapper;

    @Operation(summary = "权限列表")
    @GetMapping
    public ApiResponse<List<PermissionDTO>> list() {
        return ApiResponse.success(permissionService.findAll().stream().map(mapper::toDto).toList());
    }

    @Operation(summary = "新增权限")
    @PostMapping
    public ApiResponse<PermissionDTO> create(@RequestBody PermissionDTO dto) {
        Permission entity = mapper.toEntity(dto);
        return ApiResponse.success(mapper.toDto(permissionService.save(entity)));
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.deleteById(id);
        return ApiResponse.success();
    }
}
