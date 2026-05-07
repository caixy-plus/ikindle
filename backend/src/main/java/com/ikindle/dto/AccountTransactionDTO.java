package com.ikindle.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountTransactionDTO {
    private Long id;
    private Long userId;
    private String transactionNo;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String content;
    private String status;
    private String relatedOrderNo;
    private LocalDateTime transactionTime;
    private String remark;
}
