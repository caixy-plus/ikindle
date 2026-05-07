package com.ikindle.mapper;

import com.ikindle.dto.SyncTaskDTO;
import com.ikindle.entity.SyncTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncTaskDtoMapper extends BaseDtoMapper<SyncTask, SyncTaskDTO> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "book.coverUrl", target = "bookCoverUrl")
    @Mapping(source = "orderItem.id", target = "orderItemId")
    SyncTaskDTO toDto(SyncTask task);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "orderItem", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    SyncTask toEntity(SyncTaskDTO dto);
}
