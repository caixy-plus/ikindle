package com.ikindle.mapper;

import com.ikindle.dto.TagDTO;
import com.ikindle.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagDtoMapper extends BaseDtoMapper<Tag, TagDTO> {
    
    @Override
    TagDTO toDto(Tag tag);
    
    @Override
    @Mapping(target = "isDeleted", ignore = true)
    Tag toEntity(TagDTO tagDTO);
}