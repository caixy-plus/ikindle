package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.TagDTO;
import com.ikindle.entity.Tag;
import com.ikindle.mapper.TagDtoMapper;
import com.ikindle.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagDtoMapper tagDtoMapper;

    @GetMapping
    public ApiResponse<List<TagDTO>> list(@RequestParam(required = false) String keyword) {
        List<Tag> tags = (keyword == null || keyword.isBlank())
                ? tagService.findEnabled()
                : tagService.searchByName(keyword);
        return ApiResponse.success(tagDtoMapper.toDtoList(tags));
    }

    @GetMapping("/popular")
    public ApiResponse<List<TagDTO>> popular() {
        return ApiResponse.success(tagDtoMapper.toDtoList(tagService.findPopularTags()));
    }

    @GetMapping("/{id}")
    public ApiResponse<TagDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(tagDtoMapper.toDto(tagService.findByIdOrThrow(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TagDTO> create(@RequestBody TagDTO dto) {
        Tag saved = tagService.save(tagDtoMapper.toEntity(dto));
        return ApiResponse.success(tagDtoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TagDTO> update(@PathVariable Long id, @RequestBody TagDTO dto) {
        dto.setId(id);
        Tag tag = tagDtoMapper.toEntity(dto);
        tag.setId(id);
        return ApiResponse.success(tagDtoMapper.toDto(tagService.update(id, tag)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tagService.deleteById(id);
        return ApiResponse.success();
    }
}
