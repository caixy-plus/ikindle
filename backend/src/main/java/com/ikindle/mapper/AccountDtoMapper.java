package com.ikindle.mapper;

import com.ikindle.dto.AccountDTO;
import com.ikindle.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountDtoMapper extends BaseDtoMapper<Account, AccountDTO> {

    @Override
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    AccountDTO toDto(Account account);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Account toEntity(AccountDTO dto);
}
