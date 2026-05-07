package com.ikindle.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RechargeOrderDTO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String username;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private String thirdPartyTxId;
    private LocalDateTime paidTime;
    private String remark;
    private LocalDateTime createdTime;
}
