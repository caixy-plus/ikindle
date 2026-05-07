package com.ikindle.service;

import com.ikindle.entity.RechargeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * 充值订单 Service
 */
public interface RechargeOrderService extends BaseService<RechargeOrder, Long> {

    /**
     * 创建充值订单(待支付)
     */
    RechargeOrder createOrder(Long userId, BigDecimal amount, String paymentMethod, String remark);

    /**
     * 充值订单支付完成 - 写流水 + 加余额
     */
    RechargeOrder markPaid(String orderNo, String thirdPartyTxId);

    RechargeOrder markFailed(String orderNo, String reason);

    RechargeOrder findByOrderNo(String orderNo);

    Page<RechargeOrder> listByUser(Long userId, Pageable pageable);
}
