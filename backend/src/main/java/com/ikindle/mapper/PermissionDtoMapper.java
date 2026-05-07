package com.ikindle.mapper;

import com.ikindle.dto.PermissionDTO;
import com.ikindle.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionDtoMapper extends BaseDtoMapper<Permission, PermissionDTO> {
}
