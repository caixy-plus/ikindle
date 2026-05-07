package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.CreateOrderRequest;
import com.ikindle.dto.OrderDTO;
import com.ikindle.dto.PageResponse;
import com.ikindle.dto.PayOrderRequest;
import com.ikindle.entity.Order;
import com.ikindle.mapper.OrderDtoMapper;
import com.ikindle.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单管理", description = "订单创建、支付、查询")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDtoMapper orderMapper;

    @Operation(summary = "创建订单")
    @PostMapping
    public ApiResponse<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ApiResponse.success(orderMapper.toDto(order));
    }

    @Operation(summary = "支付订单")
    @PostMapping("/{orderId}/pay")
    public ApiResponse<OrderDTO> payOrder(@PathVariable Long orderId,
                                          @Valid @RequestBody PayOrderRequest request) {
        Order.PaymentMethod method = Order.PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        Order order = orderService.payOrder(orderId, method);
        return ApiResponse.success(orderMapper.toDto(order));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        Order order = orderService.cancelOrder(orderId);
        return ApiResponse.success(orderMapper.toDto(order));
    }

    @Operation(summary = "根据订单号查询")
    @GetMapping("/no/{orderNo}")
    public ApiResponse<OrderDTO> getByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.findByOrderNo(orderNo);
        return ApiResponse.success(orderMapper.toDto(order));
    }

    @Operation(summary = "根据ID查询订单")
    @GetMapping("/{orderId}")
    public ApiResponse<OrderDTO> getOrder(@PathVariable Long orderId) {
        Order order = orderService.findByIdOrThrow(orderId);
        return ApiResponse.success(orderMapper.toDto(order));
    }

    @Operation(summary = "查询用户订单")
    @GetMapping("/user/{userId}")
    public ApiResponse<PageResponse<OrderDTO>> listByUser(@PathVariable Long userId,
                                                          @RequestParam(required = false) Order.OrderStatus status,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> result = (status != null)
                ? orderService.findByUserIdAndStatus(userId, status, pageable)
                : orderService.findByUserId(userId, pageable);
        List<OrderDTO> items = result.getContent().stream().map(orderMapper::toDto).toList();
        return ApiResponse.success(PageResponse.of(items, page, size, result.getTotalElements()));
    }

    @Operation(summary = "管理端 - 全部订单(分状态)")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<OrderDTO>> adminList(@RequestParam(required = false) Order.OrderStatus status,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> result = (status != null)
                ? orderService.findByStatus(status, pageable)
                : orderService.findAll(pageable);
        List<OrderDTO> items = result.getContent().stream().map(orderMapper::toDto).toList();
        return ApiResponse.success(PageResponse.of(items, page, size, result.getTotalElements()));
    }
}
