package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户流水实体类
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "account_transactions")
public class AccountTransaction extends BaseEntity {

    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 交易流水号
     */
    @Column(name = "transaction_no", unique = true, nullable = false, length = 50)
    private String transactionNo;

    /**
     * 交易类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    /**
     * 交易金额
     */
    @NotNull(message = "交易金额不能为空")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 交易前余额
     */
    @Column(name = "balance_before", precision = 10, scale = 2)
    private BigDecimal balanceBefore;

    /**
     * 交易后余额
     */
    @Column(name = "balance_after", precision = 10, scale = 2)
    private BigDecimal balanceAfter;

    /**
     * 交易内容
     */
    @Column(name = "content", length = 500)
    private String content;

    /**
     * 交易状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status = TransactionStatus.SUCCESS;

    /**
     * 关联订单号
     */
    @Column(name = "related_order_no", length = 50)
    private String relatedOrderNo;

    /**
     * 交易时间
     */
    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

    // 交易类型枚举
    public enum TransactionType {
        RECHARGE,       // 充值
        CONSUME,        // 消费
        REFUND,         // 退款
        FREEZE,         // 冻结
        UNFREEZE,       // 解冻
        TRANSFER_IN,    // 转入
        TRANSFER_OUT    // 转出
    }

    // 交易状态枚举
    public enum TransactionStatus {
        PENDING,    // 处理中
        SUCCESS,    // 成功
        FAILED,     // 失败
        CANCELLED   // 已取消
    }

} 