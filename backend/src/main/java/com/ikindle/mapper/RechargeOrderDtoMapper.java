package com.ikindle.mapper;

import com.ikindle.dto.RechargeOrderDTO;
import com.ikindle.entity.RechargeOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RechargeOrderDtoMapper extends BaseDtoMapper<RechargeOrder, RechargeOrderDTO> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    RechargeOrderDTO toDto(RechargeOrder order);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    RechargeOrder toEntity(RechargeOrderDTO dto);
}
