package com.ikindle.mapper;

import com.ikindle.dto.OrderDTO;
import com.ikindle.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemDtoMapper.class})
public interface OrderDtoMapper extends BaseDtoMapper<Order, OrderDTO> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    OrderDTO toDto(Order order);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Order toEntity(OrderDTO dto);
}
