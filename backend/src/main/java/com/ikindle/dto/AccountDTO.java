package com.ikindle.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDTO {
    private Long id;
    private Long userId;
    private String username;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private BigDecimal frozenBalance;
    private BigDecimal totalRecharge;
    private BigDecimal totalConsumption;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
