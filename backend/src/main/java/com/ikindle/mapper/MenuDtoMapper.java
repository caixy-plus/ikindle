package com.ikindle.mapper;

import com.ikindle.dto.MenuDTO;
import com.ikindle.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuDtoMapper extends BaseDtoMapper<Menu, MenuDTO> {

    @Override
    @Mapping(target = "children", ignore = true)
    MenuDTO toDto(Menu menu);

    @Override
    @Mapping(target = "isDeleted", ignore = true)
    Menu toEntity(MenuDTO dto);
}
