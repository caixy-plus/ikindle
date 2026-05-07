package com.ikindle.mapper;

import com.ikindle.dto.SyncSettingDTO;
import com.ikindle.entity.SyncSetting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncSettingDtoMapper extends BaseDtoMapper<SyncSetting, SyncSettingDTO> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    SyncSettingDTO toDto(SyncSetting setting);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    SyncSetting toEntity(SyncSettingDTO dto);
}
