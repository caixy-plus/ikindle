package com.ikindle.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayOrderRequest {
    @NotNull(message = "支付方式不能为空")
    private String paymentMethod;
}
