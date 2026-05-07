package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.ConfigDefinitionDTO;
import com.ikindle.dto.SystemConfigDTO;
import com.ikindle.entity.ConfigDefinition;
import com.ikindle.mapper.ConfigDefinitionDtoMapper;
import com.ikindle.mapper.SystemConfigDtoMapper;
import com.ikindle.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "系统参数", description = "运行时配置 + 配置定义")
@RestController
@RequestMapping("/api/system-config")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;
    private final SystemConfigDtoMapper systemConfigMapper;
    private final ConfigDefinitionDtoMapper definitionMapper;

    @Operation(summary = "读取配置")
    @GetMapping("/{key}")
    public ApiResponse<String> get(@PathVariable String key) {
        return ApiResponse.success(systemConfigService.get(key));
    }

    @Operation(summary = "设置配置")
    @PutMapping("/{key}")
    public ApiResponse<SystemConfigDTO> set(@PathVariable String key, @RequestParam String value) {
        return ApiResponse.success(systemConfigMapper.toDto(systemConfigService.set(key, value)));
    }

    @Operation(summary = "查询配置定义")
    @GetMapping("/definitions")
    public ApiResponse<List<ConfigDefinitionDTO>> listDefinitions(@RequestParam(required = false) String category) {
        List<ConfigDefinition> list = systemConfigService.listDefinitions(category);
        return ApiResponse.success(list.stream().map(definitionMapper::toDto).toList());
    }

    @Operation(summary = "新增配置定义")
    @PostMapping("/definitions")
    public ApiResponse<ConfigDefinitionDTO> registerDefinition(@RequestBody ConfigDefinitionDTO dto) {
        ConfigDefinition entity = definitionMapper.toEntity(dto);
        return ApiResponse.success(definitionMapper.toDto(systemConfigService.registerDefinition(entity)));
    }
}
