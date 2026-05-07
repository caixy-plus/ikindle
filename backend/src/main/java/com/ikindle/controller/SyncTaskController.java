package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.PageResponse;
import com.ikindle.dto.SyncSettingDTO;
import com.ikindle.dto.SyncTaskDTO;
import com.ikindle.entity.SyncSetting;
import com.ikindle.entity.SyncTask;
import com.ikindle.mapper.SyncSettingDtoMapper;
import com.ikindle.mapper.SyncTaskDtoMapper;
import com.ikindle.service.SyncTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "同步任务", description = "电子书同步到 Kindle 邮箱 + 同步设置")
@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncTaskController {

    private final SyncTaskService syncTaskService;
    private final SyncTaskDtoMapper taskMapper;
    private final SyncSettingDtoMapper settingMapper;

    @Operation(summary = "提交同步任务")
    @PostMapping("/tasks")
    public ApiResponse<SyncTaskDTO> enqueue(@RequestParam Long userId,
                                            @RequestParam Long bookId,
                                            @RequestParam(required = false) Long orderItemId) {
        return ApiResponse.success(taskMapper.toDto(syncTaskService.enqueue(userId, bookId, orderItemId)));
    }

    @Operation(summary = "重试任务")
    @PostMapping("/tasks/{id}/retry")
    public ApiResponse<SyncTaskDTO> retry(@PathVariable Long id) {
        return ApiResponse.success(taskMapper.toDto(syncTaskService.retry(id)));
    }

    @Operation(summary = "取消任务")
    @PostMapping("/tasks/{id}/cancel")
    public ApiResponse<SyncTaskDTO> cancel(@PathVariable Long id) {
        return ApiResponse.success(taskMapper.toDto(syncTaskService.cancel(id)));
    }

    @Operation(summary = "我的同步任务")
    @GetMapping("/tasks/user/{userId}")
    public ApiResponse<PageResponse<SyncTaskDTO>> listByUser(@PathVariable Long userId,
                                                             @RequestParam(required = false) SyncTask.TaskStatus status,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SyncTask> result = syncTaskService.listByUser(userId, status, pageable);
        List<SyncTaskDTO> items = result.getContent().stream().map(taskMapper::toDto).toList();
        return ApiResponse.success(PageResponse.of(items, page, size, result.getTotalElements()));
    }

    @Operation(summary = "查询同步设置")
    @GetMapping("/settings/user/{userId}")
    public ApiResponse<SyncSettingDTO> getSetting(@PathVariable Long userId) {
        return ApiResponse.success(settingMapper.toDto(syncTaskService.getSetting(userId)));
    }

    @Operation(summary = "更新同步设置")
    @PutMapping("/settings/user/{userId}")
    public ApiResponse<SyncSettingDTO> updateSetting(@PathVariable Long userId,
                                                     @RequestBody SyncSettingDTO dto) {
        SyncSetting.Priority priority = dto.getPriority() == null ? null : SyncSetting.Priority.valueOf(dto.getPriority());
        SyncSetting saved = syncTaskService.updateSetting(userId, dto.getKindleEmail(), dto.getAutoSync(), priority, dto.getPreferredFormat());
        return ApiResponse.success(settingMapper.toDto(saved));
    }

    @Operation(summary = "手动触发同步任务执行（管理端）")
    @PostMapping("/execute")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> executePending() {
        syncTaskService.executePending();
        return ApiResponse.success();
    }
}
