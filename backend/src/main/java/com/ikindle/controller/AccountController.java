package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.AccountDTO;
import com.ikindle.dto.AccountTransactionDTO;
import com.ikindle.dto.PageResponse;
import com.ikindle.dto.RechargeRequest;
import com.ikindle.entity.Account;
import com.ikindle.entity.AccountTransaction;
import com.ikindle.mapper.AccountDtoMapper;
import com.ikindle.mapper.AccountTransactionDtoMapper;
import com.ikindle.service.AccountService;
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

@Tag(name = "账户管理", description = "账户余额、充值、流水")
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountDtoMapper accountMapper;
    private final AccountTransactionDtoMapper transactionMapper;

    @Operation(summary = "查询用户账户")
    @GetMapping("/user/{userId}")
    public ApiResponse<AccountDTO> getAccount(@PathVariable Long userId) {
        Account account = accountService.findByUserId(userId);
        return ApiResponse.success(accountMapper.toDto(account));
    }

    @Operation(summary = "充值")
    @PostMapping("/recharge/{userId}")
    public ApiResponse<AccountTransactionDTO> recharge(@PathVariable Long userId,
                                                        @Valid @RequestBody RechargeRequest request) {
        AccountTransaction tx = accountService.recharge(userId, request.getAmount(),
                request.getPaymentMethod(), request.getRemark());
        return ApiResponse.success(transactionMapper.toDto(tx));
    }

    @Operation(summary = "查询交易流水")
    @GetMapping("/transactions/{userId}")
    public ApiResponse<PageResponse<AccountTransactionDTO>> listTransactions(@PathVariable Long userId,
                                                                             @RequestParam(required = false) AccountTransaction.TransactionType type,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountTransaction> result = accountService.findTransactions(userId, type, pageable);
        List<AccountTransactionDTO> items = result.getContent().stream().map(transactionMapper::toDto).toList();
        return ApiResponse.success(PageResponse.of(items, page, size, result.getTotalElements()));
    }

    @Operation(summary = "管理端 - 退款")
    @PostMapping("/refund/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AccountTransactionDTO> refund(@PathVariable Long userId,
                                                      @RequestParam java.math.BigDecimal amount,
                                                      @RequestParam String orderNo,
                                                      @RequestParam(required = false, defaultValue = "管理员退款") String content) {
        AccountTransaction tx = accountService.refund(userId, amount, orderNo, content);
        return ApiResponse.success(transactionMapper.toDto(tx));
    }
}
