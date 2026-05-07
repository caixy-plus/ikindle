package com.ikindle.mapper;

import java.util.Collection;
import java.util.List;

public interface BaseDtoMapper<E, D> {
    D toDto(E e);

    E toEntity(D d);

    default List<D> toDtoList(Collection<E> eList) {
        return eList.stream().map(this::toDto).toList();
    }

    default List<E> toEntityList(Collection<D> dList) {
        return dList.stream().map(this::toEntity).toList();
    }
}