package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值订单实体
 *
 * 与 AccountTransaction 联动:充值成功后写 RECHARGE 流水并增加可用余额
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "recharge_orders")
public class RechargeOrder extends BaseEntity {

    @Column(name = "order_no", unique = true, nullable = false, length = 50)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "third_party_tx_id", length = 100)
    private String thirdPartyTxId;

    @Column(name = "paid_time")
    private LocalDateTime paidTime;

    @Column(name = "remark", length = 500)
    private String remark;

    public enum PaymentStatus {
        PENDING, PAID, FAILED, CANCELLED, REFUNDED
    }
}
