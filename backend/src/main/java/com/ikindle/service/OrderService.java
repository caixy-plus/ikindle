package com.ikindle.service;

import com.ikindle.dto.CreateOrderRequest;
import com.ikindle.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 订单 Service
 */
public interface OrderService extends BaseService<Order, Long> {

    Order createOrder(CreateOrderRequest request);

    Order payOrder(Long orderId, Order.PaymentMethod paymentMethod);

    Order cancelOrder(Long orderId);

    Order findByOrderNo(String orderNo);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status, Pageable pageable);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
}
