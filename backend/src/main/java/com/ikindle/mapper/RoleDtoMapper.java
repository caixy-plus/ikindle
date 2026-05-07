package com.ikindle.mapper;

import com.ikindle.dto.RoleDTO;
import com.ikindle.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PermissionDtoMapper.class})
public interface RoleDtoMapper extends BaseDtoMapper<Role, RoleDTO> {

    @Override
    @Mapping(target = "isDeleted", ignore = true)
    Role toEntity(RoleDTO dto);
}
