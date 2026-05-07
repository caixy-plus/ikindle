package com.ikindle.auth.oauth;

import com.ikindle.common.ApiResponse;
import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.entity.UserOAuthSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * OAuth 认证控制器
 * 对齐 brain-think 的 AuthController
 * 端点: /api/oauth/exchange, /refresh, /logout, /me
 */
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthAuthController {

    private final OAuthSessionService oauthSessionService;

    @PostMapping("/exchange")
    public ApiResponse<OAuthDtos.ExchangeResponse> exchange(@Valid @RequestBody OAuthDtos.ExchangeRequest req) {
        return ApiResponse.success(oauthSessionService.exchange(req.getCode()));
    }

    @PostMapping("/refresh")
    public ApiResponse<OAuthDtos.ExchangeResponse> refresh() {
        String sessionKey = currentSessionKey();
        return ApiResponse.success(oauthSessionService.refresh(sessionKey));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        String sessionKey = currentSessionKey();
        oauthSessionService.logout(sessionKey);
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<OAuthDtos.MeResponse> me() {
        String sessionKey = currentSessionKey();
        UserOAuthSession s = oauthSessionService.require(sessionKey);
        return ApiResponse.success(new OAuthDtos.MeResponse(
                s.getSessionKey(), s.getProviderUserId(), s.getEmail(), s.getDisplayName()));
    }

    private String currentSessionKey() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        // principal 是 username (String),sessionKey 需要从请求中提取
        // 对于 refresh/logout/me,它们需要 sessionKey 来找到对应的 OAuth session
        // 我们从 Authorization header 中解析 token,然后提取 sessionKey claim
        // 这里简化处理:直接从 SecurityContext 的 details 中获取
        Object details = auth.getDetails();
        if (details instanceof String sessionKey && !sessionKey.isBlank()) {
            return sessionKey;
        }
        throw new BusinessException(ErrorCode.UNAUTHORIZED, "无法获取会话标识");
    }
}
