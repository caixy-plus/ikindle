package com.ikindle.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务错误码
 * 每个错误码对应一个 HTTP 状态码(供全局异常处理器使用)
 */
@Getter
public enum ErrorCode {

    SUCCESS(200, "操作成功", HttpStatus.OK),
    BAD_REQUEST(400, "请求参数错误", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "未授权或登录已过期", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "无权访问", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "资源不存在", HttpStatus.NOT_FOUND),
    CONFLICT(409, "资源冲突", HttpStatus.CONFLICT),
    VALIDATION_FAILED(422, "参数校验失败", HttpStatus.UNPROCESSABLE_ENTITY),
    INTERNAL_ERROR(500, "服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR),

    // 用户域 1000-1099
    USER_NOT_FOUND(1001, "用户不存在", HttpStatus.BAD_REQUEST),
    USER_DISABLED(1002, "用户已禁用", HttpStatus.FORBIDDEN),
    PASSWORD_INCORRECT(1003, "密码错误", HttpStatus.UNAUTHORIZED),
    USERNAME_EXISTS(1004, "用户名已存在", HttpStatus.CONFLICT),
    EMAIL_EXISTS(1005, "邮箱已存在", HttpStatus.CONFLICT),
    PHONE_EXISTS(1006, "手机号已存在", HttpStatus.CONFLICT),
    OLD_PASSWORD_INCORRECT(1007, "原密码错误", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(1008, "Token无效", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1009, "Token已过期", HttpStatus.UNAUTHORIZED),

    // 图书域 2000-2099
    BOOK_NOT_FOUND(2001, "图书不存在", HttpStatus.NOT_FOUND),
    BOOK_OFF_SHELF(2002, "图书已下架", HttpStatus.BAD_REQUEST),
    STOCK_INSUFFICIENT(2003, "库存不足", HttpStatus.BAD_REQUEST),

    // 订单域 3000-3099
    ORDER_NOT_FOUND(3001, "订单不存在", HttpStatus.NOT_FOUND),
    ORDER_STATUS_ILLEGAL(3002, "订单状态非法", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_PAID(3003, "订单已支付", HttpStatus.BAD_REQUEST),

    // 账户域 4000-4099
    ACCOUNT_NOT_FOUND(4001, "账户不存在", HttpStatus.NOT_FOUND),
    BALANCE_INSUFFICIENT(4002, "余额不足", HttpStatus.BAD_REQUEST),
    ACCOUNT_FROZEN(4003, "账户已冻结", HttpStatus.FORBIDDEN),
    RECHARGE_FAILED(4004, "充值失败", HttpStatus.BAD_REQUEST),

    // 书架域 5000-5099
    BOOKSHELF_ITEM_EXISTS(5001, "图书已在书架中", HttpStatus.CONFLICT),

    // 同步域 6000-6099
    SYNC_TASK_FAILED(6001, "同步任务失败", HttpStatus.BAD_REQUEST),
    KINDLE_EMAIL_NOT_SET(6002, "未设置Kindle邮箱", HttpStatus.BAD_REQUEST),

    // 文件域 7000-7099
    FILE_UPLOAD_FAILED(7001, "文件上传失败", HttpStatus.BAD_REQUEST),
    FILE_TYPE_NOT_ALLOWED(7002, "文件类型不允许", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED(7003, "文件大小超出限制", HttpStatus.BAD_REQUEST),

    // OAuth 域 8000-8099
    OAUTH_EXCHANGE_FAILED(8001, "OAuth 授权码换 Token 失败: %s", HttpStatus.UNAUTHORIZED),
    OAUTH_REFRESH_FAILED(8002, "OAuth Token 刷新失败: %s", HttpStatus.UNAUTHORIZED),
    OAUTH_SESSION_NOT_FOUND(8003, "OAuth 会话不存在", HttpStatus.UNAUTHORIZED),
    OAUTH_PLATFORM_ERROR(8004, "OAuth 平台调用失败", HttpStatus.BAD_GATEWAY),
    OAUTH_NOT_CONFIGURED(8005, "OAuth 平台未配置", HttpStatus.BAD_REQUEST),
    OAUTH_CODE_REQUIRED(8006, "授权码不能为空", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
