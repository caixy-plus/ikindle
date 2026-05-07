package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.PageResponse;
import com.ikindle.dto.UserBookshelfDTO;
import com.ikindle.entity.UserBookshelf;
import com.ikindle.mapper.UserBookshelfDtoMapper;
import com.ikindle.service.UserBookshelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户书架", description = "用户书架管理")
@RestController
@RequestMapping("/api/bookshelf")
@RequiredArgsConstructor
public class UserBookshelfController {

    private final UserBookshelfService bookshelfService;
    private final UserBookshelfDtoMapper mapper;

    @Operation(summary = "查询用户书架(支持 syncStatus tab 筛选)")
    @GetMapping("/user/{userId}")
    public ApiResponse<PageResponse<UserBookshelfDTO>> listMy(@PathVariable Long userId,
                                                              @RequestParam(required = false) UserBookshelf.SyncStatus syncStatus,
                                                              @RequestParam(required = false) Boolean favoriteOnly,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserBookshelf> result = bookshelfService.listByUser(userId, syncStatus, favoriteOnly, pageable);
        List<UserBookshelfDTO> items = result.getContent().stream().map(mapper::toDto).toList();
        return ApiResponse.success(PageResponse.of(items, page, size, result.getTotalElements()));
    }

    @Operation(summary = "添加到书架")
    @PostMapping("/add")
    public ApiResponse<UserBookshelfDTO> add(@RequestParam Long userId, @RequestParam Long bookId) {
        UserBookshelf saved = bookshelfService.addOrUpdate(userId, bookId);
        return ApiResponse.success(mapper.toDto(saved));
    }

    @Operation(summary = "更新阅读进度")
    @PutMapping("/{id}/progress")
    public ApiResponse<UserBookshelfDTO> updateProgress(@PathVariable Long id,
                                                        @RequestParam Integer progress,
                                                        @RequestParam Double percentage) {
        UserBookshelf saved = bookshelfService.updateProgress(id, progress, percentage);
        return ApiResponse.success(mapper.toDto(saved));
    }

    @Operation(summary = "切换收藏")
    @PutMapping("/{id}/favorite")
    public ApiResponse<UserBookshelfDTO> toggleFavorite(@PathVariable Long id) {
        UserBookshelf saved = bookshelfService.toggleFavorite(id);
        return ApiResponse.success(mapper.toDto(saved));
    }

    @Operation(summary = "更新同步状态")
    @PutMapping("/{id}/sync-status")
    public ApiResponse<UserBookshelfDTO> updateSyncStatus(@PathVariable Long id,
                                                          @RequestParam UserBookshelf.SyncStatus status) {
        UserBookshelf saved = bookshelfService.updateSyncStatus(id, status);
        return ApiResponse.success(mapper.toDto(saved));
    }

    @Operation(summary = "从书架移除")
    @DeleteMapping
    public ApiResponse<Void> remove(@RequestParam Long userId, @RequestParam Long bookId) {
        bookshelfService.removeByUserAndBook(userId, bookId);
        return ApiResponse.success();
    }
}
