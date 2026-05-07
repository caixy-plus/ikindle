package com.ikindle.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String username;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private String status;
    private String paymentMethod;
    private LocalDateTime payTime;
    private String remark;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
