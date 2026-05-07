package com.ikindle.repository;

import com.ikindle.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单Repository接口
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {

    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查找订单
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和状态查找订单
     */
    Page<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status, Pageable pageable);

    /**
     * 根据状态查找订单
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    /**
     * 根据支付方式查找订单
     */
    Page<Order> findByPaymentMethod(Order.PaymentMethod paymentMethod, Pageable pageable);

    /**
     * 根据创建时间范围查找订单
     */
    @Query("SELECT o FROM Order o WHERE o.createdTime BETWEEN :startTime AND :endTime")
    Page<Order> findByCreatedTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                        @Param("endTime") LocalDateTime endTime, 
                                        Pageable pageable);

    /**
     * 根据支付时间范围查找订单
     */
    @Query("SELECT o FROM Order o WHERE o.payTime BETWEEN :startTime AND :endTime")
    Page<Order> findByPayTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                    @Param("endTime") LocalDateTime endTime, 
                                    Pageable pageable);

    /**
     * 统计用户订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户已支付订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.status = 'PAID'")
    Long countPaidOrdersByUserId(@Param("userId") Long userId);

    /**
     * 查找待处理的订单
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'PAID') ORDER BY o.createdTime ASC")
    List<Order> findPendingOrders();

    /**
     * 根据订单号检查订单是否存在
     */
    boolean existsByOrderNo(String orderNo);
} 