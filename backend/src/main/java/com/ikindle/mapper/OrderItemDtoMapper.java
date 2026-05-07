package com.ikindle.mapper;

import com.ikindle.dto.OrderItemDTO;
import com.ikindle.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemDtoMapper extends BaseDtoMapper<OrderItem, OrderItemDTO> {

    @Override
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "book.author", target = "bookAuthor")
    @Mapping(source = "book.coverUrl", target = "bookCoverUrl")
    OrderItemDTO toDto(OrderItem orderItem);

    @Override
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    OrderItem toEntity(OrderItemDTO dto);
}
