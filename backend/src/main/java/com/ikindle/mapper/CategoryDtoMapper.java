package com.ikindle.mapper;

import com.ikindle.dto.CategoryDTO;
import com.ikindle.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryDtoMapper extends BaseDtoMapper<Category, CategoryDTO> {
    
    @Override
    CategoryDTO toDto(Category category);
    
    @Override
    @Mapping(target = "isDeleted", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);
}