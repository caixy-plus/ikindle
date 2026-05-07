package com.ikindle.mapper;

import com.ikindle.dto.UserDTO;
import com.ikindle.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper extends BaseDtoMapper<User, UserDTO> {
    
    @Override
    @Mapping(target = "roles", ignore = true)
    UserDTO toDto(User user);
    
    @Override
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    User toEntity(UserDTO userDTO);
}