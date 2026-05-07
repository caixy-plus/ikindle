package com.ikindle.mapper;

import com.ikindle.dto.ConfigDefinitionDTO;
import com.ikindle.entity.ConfigDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConfigDefinitionDtoMapper extends BaseDtoMapper<ConfigDefinition, ConfigDefinitionDTO> {

    @Override
    @Mapping(target = "isDeleted", ignore = true)
    ConfigDefinition toEntity(ConfigDefinitionDTO dto);
}
