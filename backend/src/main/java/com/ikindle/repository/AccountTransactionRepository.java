package com.ikindle.repository;

import com.ikindle.entity.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * 账户流水 Repository
 */
@Repository
public interface AccountTransactionRepository extends BaseRepository<AccountTransaction, Long> {

    Page<AccountTransaction> findByUserIdOrderByTransactionTimeDesc(Long userId, Pageable pageable);

    Page<AccountTransaction> findByUserIdAndTransactionTypeOrderByTransactionTimeDesc(
            Long userId, AccountTransaction.TransactionType type, Pageable pageable);
}
