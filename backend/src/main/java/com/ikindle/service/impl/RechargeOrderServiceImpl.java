package com.ikindle.service.impl;

import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.entity.RechargeOrder;
import com.ikindle.entity.User;
import com.ikindle.repository.RechargeOrderRepository;
import com.ikindle.repository.UserRepository;
import com.ikindle.service.AccountService;
import com.ikindle.service.RechargeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class RechargeOrderServiceImpl extends BaseServiceImpl<RechargeOrder, Long> implements RechargeOrderService {

    private final RechargeOrderRepository rechargeOrderRepository;
    private final UserRepository userRepository;
    private final AccountService accountService;

    public RechargeOrderServiceImpl(RechargeOrderRepository rechargeOrderRepository,
                                    UserRepository userRepository,
                                    AccountService accountService) {
        super(rechargeOrderRepository);
        this.rechargeOrderRepository = rechargeOrderRepository;
        this.userRepository = userRepository;
        this.accountService = accountService;
    }

    @Override
    public RechargeOrder createOrder(Long userId, BigDecimal amount, String paymentMethod, String remark) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        RechargeOrder order = new RechargeOrder();
        order.setOrderNo(generateOrderNo());
        order.setUser(user);
        order.setAmount(amount);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(RechargeOrder.PaymentStatus.PENDING);
        order.setRemark(remark);
        return rechargeOrderRepository.save(order);
    }

    @Override
    public RechargeOrder markPaid(String orderNo, String thirdPartyTxId) {
        RechargeOrder order = findByOrderNo(orderNo);
        if (order.getPaymentStatus() != RechargeOrder.PaymentStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ILLEGAL, "充值订单状态非法");
        }
        order.setPaymentStatus(RechargeOrder.PaymentStatus.PAID);
        order.setThirdPartyTxId(thirdPartyTxId);
        order.setPaidTime(LocalDateTime.now());
        rechargeOrderRepository.save(order);

        accountService.recharge(order.getUser().getId(), order.getAmount(),
                order.getPaymentMethod(), "充值订单 " + order.getOrderNo());

        log.info("充值成功 orderNo={} amount={}", order.getOrderNo(), order.getAmount());
        return order;
    }

    @Override
    public RechargeOrder markFailed(String orderNo, String reason) {
        RechargeOrder order = findByOrderNo(orderNo);
        order.setPaymentStatus(RechargeOrder.PaymentStatus.FAILED);
        order.setRemark(reason);
        return rechargeOrderRepository.save(order);
    }

    @Override
    public RechargeOrder findByOrderNo(String orderNo) {
        return rechargeOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "充值订单不存在"));
    }

    @Override
    public Page<RechargeOrder> listByUser(Long userId, Pageable pageable) {
        return rechargeOrderRepository.findByUserIdOrderByCreatedTimeDesc(userId, pageable);
    }

    private String generateOrderNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "RC" + ts + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
