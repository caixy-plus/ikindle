package com.ikindle.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotEmpty(message = "购买图书不能为空")
    private List<OrderItemRequest> items;

    private String remark;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "图书ID不能为空")
        private Long bookId;
        private Integer quantity = 1;
    }
}
