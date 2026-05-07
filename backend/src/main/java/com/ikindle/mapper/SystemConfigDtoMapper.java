package com.ikindle.mapper;

import com.ikindle.dto.SystemConfigDTO;
import com.ikindle.entity.SystemConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SystemConfigDtoMapper extends BaseDtoMapper<SystemConfig, SystemConfigDTO> {

    @Override
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "valueType", ignore = true)
    @Mapping(target = "description", ignore = true)
    SystemConfigDTO toDto(SystemConfig config);

    @Override
    @Mapping(target = "isDeleted", ignore = true)
    SystemConfig toEntity(SystemConfigDTO dto);
}
