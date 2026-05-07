package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.CategoryDTO;
import com.ikindle.entity.Category;
import com.ikindle.mapper.CategoryDtoMapper;
import com.ikindle.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryDtoMapper categoryDtoMapper;

    @GetMapping
    public ApiResponse<List<CategoryDTO>> getAllCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long parentId) {
        List<Category> categories;
        if (keyword != null && !keyword.isBlank()) {
            categories = categoryService.findByNameContaining(keyword);
        } else if (parentId != null) {
            categories = categoryService.findByParentIdAndEnabledOrderBySortOrderAsc(parentId, true);
        } else {
            categories = categoryService.findByEnabledOrderBySortOrderAsc(true);
        }
        return ApiResponse.success(categoryDtoMapper.toDtoList(categories));
    }

    @GetMapping("/tree")
    public ApiResponse<List<CategoryDTO>> getCategoryTree() {
        return ApiResponse.success(categoryDtoMapper.toDtoList(categoryService.findTopLevelCategories()));
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ApiResponse.success(categoryDtoMapper.toDto(categoryService.findByIdOrThrow(id)));
    }

    @GetMapping("/{id}/book-count")
    public ApiResponse<Long> countBooks(@PathVariable Long id) {
        return ApiResponse.success(categoryService.countBooksByCategoryId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
        Category saved = categoryService.save(categoryDtoMapper.toEntity(dto));
        return ApiResponse.success(categoryDtoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        dto.setId(id);
        Category category = categoryDtoMapper.toEntity(dto);
        category.setId(id);
        Category updated = categoryService.update(id, category);
        return ApiResponse.success(categoryDtoMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ApiResponse.success();
    }
}
