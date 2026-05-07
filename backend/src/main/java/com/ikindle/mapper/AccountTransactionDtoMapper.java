package com.ikindle.mapper;

import com.ikindle.dto.AccountTransactionDTO;
import com.ikindle.entity.AccountTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountTransactionDtoMapper extends BaseDtoMapper<AccountTransaction, AccountTransactionDTO> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    AccountTransactionDTO toDto(AccountTransaction tx);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    AccountTransaction toEntity(AccountTransactionDTO dto);
}
