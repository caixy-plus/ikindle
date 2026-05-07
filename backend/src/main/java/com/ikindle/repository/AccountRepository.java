package com.ikindle.repository;

import com.ikindle.entity.Account;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 账户 Repository
 */
@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {

    Optional<Account> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
