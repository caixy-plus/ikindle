package com.ikindle.mapper;

import com.ikindle.dto.UserBookshelfDTO;
import com.ikindle.entity.UserBookshelf;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserBookshelfDtoMapper extends BaseDtoMapper<UserBookshelf, UserBookshelfDTO> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "book.author", target = "bookAuthor")
    @Mapping(source = "book.coverUrl", target = "bookCoverUrl")
    @Mapping(source = "book.description", target = "bookDescription")
    UserBookshelfDTO toDto(UserBookshelf bookshelf);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    UserBookshelf toEntity(UserBookshelfDTO dto);
}
