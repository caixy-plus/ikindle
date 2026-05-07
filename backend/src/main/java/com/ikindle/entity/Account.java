package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 用户账户实体类
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    /**
     * 用户
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * 账户余额
     */
    @NotNull(message = "账户余额不能为空")
    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 可用余额
     */
    @NotNull(message = "可用余额不能为空")
    @Column(name = "available_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    /**
     * 冻结余额
     */
    @NotNull(message = "冻结余额不能为空")
    @Column(name = "frozen_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal frozenBalance = BigDecimal.ZERO;

    /**
     * 累计充值金额
     */
    @Column(name = "total_recharge", precision = 10, scale = 2)
    private BigDecimal totalRecharge = BigDecimal.ZERO;

    /**
     * 累计消费金额
     */
    @Column(name = "total_consumption", precision = 10, scale = 2)
    private BigDecimal totalConsumption = BigDecimal.ZERO;

    /**
     * 账户状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

    // 账户状态枚举
    public enum AccountStatus {
        ACTIVE,     // 正常
        FROZEN,     // 冻结
        CLOSED      // 关闭
    }

} 