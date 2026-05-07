package com.ikindle.repository;

import com.ikindle.entity.RechargeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RechargeOrderRepository extends BaseRepository<RechargeOrder, Long> {

    Optional<RechargeOrder> findByOrderNo(String orderNo);

    Page<RechargeOrder> findByUserIdOrderByCreatedTimeDesc(Long userId, Pageable pageable);

    Page<RechargeOrder> findByPaymentStatusOrderByCreatedTimeDesc(RechargeOrder.PaymentStatus status, Pageable pageable);
}
