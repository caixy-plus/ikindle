package com.ikindle.service;

import com.ikindle.entity.Account;
import com.ikindle.entity.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * 账户 Service
 */
public interface AccountService extends BaseService<Account, Long> {

    Account createForUser(Long userId);

    Account findByUserId(Long userId);

    /**
     * 充值
     */
    AccountTransaction recharge(Long userId, BigDecimal amount, String paymentMethod, String remark);

    /**
     * 消费(订单支付时调用,使用 Redisson 分布式锁)
     */
    AccountTransaction consume(Long userId, BigDecimal amount, String orderNo, String content);

    /**
     * 退款
     */
    AccountTransaction refund(Long userId, BigDecimal amount, String orderNo, String content);

    Page<AccountTransaction> findTransactions(Long userId, AccountTransaction.TransactionType type, Pageable pageable);
}
