package com.ikindle.mapper;

import com.ikindle.dto.BookDTO;
import com.ikindle.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryDtoMapper.class, TagDtoMapper.class})
public interface BookDtoMapper extends BaseDtoMapper<Book, BookDTO> {
    
    @Override
    BookDTO toDto(Book book);
    
    @Override
    @Mapping(target = "isDeleted", ignore = true)
    Book toEntity(BookDTO bookDTO);
}