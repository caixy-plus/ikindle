package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.PageResponse;
import com.ikindle.dto.RechargeOrderDTO;
import com.ikindle.entity.RechargeOrder;
import com.ikindle.mapper.RechargeOrderDtoMapper;
import com.ikindle.service.RechargeOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "充值订单", description = "充值流程")
@RestController
@RequestMapping("/api/recharge-orders")
@RequiredArgsConstructor
public class RechargeOrderController {

    private final RechargeOrderService rechargeOrderService;
    private final RechargeOrderDtoMapper mapper;

    @Operation(summary = "创建充值订单")
    @PostMapping
    public ApiResponse<RechargeOrderDTO> create(@RequestParam Long userId,
                                                @RequestParam BigDecimal amount,
                                                @RequestParam String paymentMethod,
                                                @RequestParam(required = false) String remark) {
        RechargeOrder order = rechargeOrderService.createOrder(userId, amount, paymentMethod, remark);
        return ApiResponse.success(mapper.toDto(order));
    }

    @Operation(summary = "支付完成回调(MOCK)")
    @PostMapping("/{orderNo}/paid")
    public ApiResponse<RechargeOrderDTO> markPaid(@PathVariable String orderNo,
                                                  @RequestParam(required = false) String thirdPartyTxId) {
        return ApiResponse.success(mapper.toDto(rechargeOrderService.markPaid(orderNo, thirdPartyTxId)));
    }

    @Operation(summary = "我的充值记录")
    @GetMapping("/user/{userId}")
    public ApiResponse<PageResponse<RechargeOrderDTO>> listMine(@PathVariable Long userId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RechargeOrder> result = rechargeOrderService.listByUser(userId, pageable);
        List<RechargeOrderDTO> items = result.getContent().stream().map(mapper::toDto).toList();
        return ApiResponse.success(PageResponse.of(items, page, size, result.getTotalElements()));
    }

    @Operation(summary = "管理端 - 全部充值订单")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<RechargeOrderDTO>> adminList(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RechargeOrder> result = rechargeOrderService.findAll(pageable);
        List<RechargeOrderDTO> items = result.getContent().stream().map(mapper::toDto).toList();
        return ApiResponse.success(PageResponse.of(items, page, size, result.getTotalElements()));
    }
}
