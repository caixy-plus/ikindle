package com.ikindle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    /**
     * 订单号
     */
    @Column(name = "order_no", unique = true, nullable = false, length = 50)
    private String orderNo;

    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 订单总金额
     */
    @NotNull(message = "订单总金额不能为空")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * 实付金额
     */
    @NotNull(message = "实付金额不能为空")
    @Column(name = "pay_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal payAmount;

    /**
     * 订单状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * 支付方式
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    /**
     * 支付时间
     */
    @Column(name = "pay_time")
    private java.time.LocalDateTime payTime;

    /**
     * 订单备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * 订单项列表
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 订单状态枚举
    public enum OrderStatus {
        PENDING,    // 待支付
        PAID,       // 已支付
        CANCELLED,  // 已取消
        REFUNDED,   // 已退款
        COMPLETED   // 已完成
    }

    // 支付方式枚举
    public enum PaymentMethod {
        ALIPAY,     // 支付宝
        WECHAT,     // 微信支付
        BALANCE     // 余额支付
    }
}