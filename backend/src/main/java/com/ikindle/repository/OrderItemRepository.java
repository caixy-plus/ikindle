package com.ikindle.repository;

import com.ikindle.entity.OrderItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单项 Repository
 */
@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.user.id = :userId AND o.status = 'PAID'")
    List<OrderItem> findPaidItemsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi JOIN oi.order o WHERE o.user.id = :userId AND oi.book.id = :bookId AND o.status = 'PAID'")
    Long countPaidByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
