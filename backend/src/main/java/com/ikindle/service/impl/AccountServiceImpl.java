package com.ikindle.service.impl;

import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.entity.Account;
import com.ikindle.entity.AccountTransaction;
import com.ikindle.entity.User;
import com.ikindle.repository.AccountRepository;
import com.ikindle.repository.AccountTransactionRepository;
import com.ikindle.repository.UserRepository;
import com.ikindle.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class AccountServiceImpl extends BaseServiceImpl<Account, Long> implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "ikindle:account:lock:";

    public AccountServiceImpl(AccountRepository accountRepository,
                              AccountTransactionRepository transactionRepository,
                              UserRepository userRepository,
                              RedissonClient redissonClient) {
        super(accountRepository);
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.redissonClient = redissonClient;
    }

    @Override
    public Account createForUser(Long userId) {
        if (accountRepository.existsByUserId(userId)) {
            return accountRepository.findByUserId(userId).orElseThrow();
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);
        account.setAvailableBalance(BigDecimal.ZERO);
        account.setFrozenBalance(BigDecimal.ZERO);
        account.setTotalRecharge(BigDecimal.ZERO);
        account.setTotalConsumption(BigDecimal.ZERO);
        account.setStatus(Account.AccountStatus.ACTIVE);
        return accountRepository.save(account);
    }

    @Override
    public Account findByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseGet(() -> createForUser(userId));
    }

    @Override
    public AccountTransaction recharge(Long userId, BigDecimal amount, String paymentMethod, String remark) {
        return withLock(userId, () -> {
            Account account = findByUserId(userId);
            ensureActive(account);

            BigDecimal before = account.getBalance();
            account.setBalance(before.add(amount));
            account.setAvailableBalance(account.getAvailableBalance().add(amount));
            account.setTotalRecharge(account.getTotalRecharge().add(amount));
            accountRepository.save(account);

            return saveTransaction(account.getUser(), AccountTransaction.TransactionType.RECHARGE,
                    amount, before, account.getBalance(),
                    "账户充值 - " + paymentMethod, null, remark);
        });
    }

    @Override
    public AccountTransaction consume(Long userId, BigDecimal amount, String orderNo, String content) {
        return withLock(userId, () -> {
            Account account = findByUserId(userId);
            ensureActive(account);
            if (account.getAvailableBalance().compareTo(amount) < 0) {
                throw new BusinessException(ErrorCode.BALANCE_INSUFFICIENT);
            }
            BigDecimal before = account.getBalance();
            account.setBalance(before.subtract(amount));
            account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
            account.setTotalConsumption(account.getTotalConsumption().add(amount));
            accountRepository.save(account);

            return saveTransaction(account.getUser(), AccountTransaction.TransactionType.CONSUME,
                    amount, before, account.getBalance(), content, orderNo, null);
        });
    }

    @Override
    public AccountTransaction refund(Long userId, BigDecimal amount, String orderNo, String content) {
        return withLock(userId, () -> {
            Account account = findByUserId(userId);
            BigDecimal before = account.getBalance();
            account.setBalance(before.add(amount));
            account.setAvailableBalance(account.getAvailableBalance().add(amount));
            accountRepository.save(account);

            return saveTransaction(account.getUser(), AccountTransaction.TransactionType.REFUND,
                    amount, before, account.getBalance(), content, orderNo, null);
        });
    }

    @Override
    public Page<AccountTransaction> findTransactions(Long userId, AccountTransaction.TransactionType type, Pageable pageable) {
        if (type == null) {
            return transactionRepository.findByUserIdOrderByTransactionTimeDesc(userId, pageable);
        }
        return transactionRepository.findByUserIdAndTransactionTypeOrderByTransactionTimeDesc(userId, type, pageable);
    }

    private void ensureActive(Account account) {
        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.ACCOUNT_FROZEN);
        }
    }

    private AccountTransaction saveTransaction(User user, AccountTransaction.TransactionType type,
                                               BigDecimal amount, BigDecimal before, BigDecimal after,
                                               String content, String orderNo, String remark) {
        AccountTransaction tx = new AccountTransaction();
        tx.setUser(user);
        tx.setTransactionNo("TX" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8));
        tx.setTransactionType(type);
        tx.setAmount(amount);
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(after);
        tx.setContent(content);
        tx.setStatus(AccountTransaction.TransactionStatus.SUCCESS);
        tx.setRelatedOrderNo(orderNo);
        tx.setTransactionTime(LocalDateTime.now());
        tx.setRemark(remark);
        return transactionRepository.save(tx);
    }

    /**
     * 使用 Redisson 分布式锁包装余额操作
     */
    private <T> T withLock(Long userId, java.util.function.Supplier<T> action) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + userId);
        try {
            boolean acquired = lock.tryLock(5, 30, TimeUnit.SECONDS);
            if (!acquired) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "账户操作繁忙,请重试");
            }
            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "账户操作被中断");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
