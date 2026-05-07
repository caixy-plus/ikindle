package com.ikindle.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.config.OAuthProperties;
import com.ikindle.entity.User;
import com.ikindle.entity.UserOAuthSession;
import com.ikindle.mapper.UserDtoMapper;
import com.ikindle.repository.UserOAuthSessionRepository;
import com.ikindle.service.UserService;
import com.ikindle.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * OAuth 会话服务
 * 核心授权 orchestration:
 * 1) exchange: code -> 平台 token -> 创建/关联本地 User -> 创建 session -> 签发本地 JWT
 * 2) refresh: 过期 JWT -> 刷新平台 token -> 更新 session -> 签发新 JWT
 * 3) logout: 撤销平台 token -> 删除 session
 * <p>
 * 对齐 brain-think 的 SessionService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthSessionService {

    private final PlatformOAuthClient oauthClient;
    private final UserOAuthSessionRepository sessionRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final UserDtoMapper userDtoMapper;
    private final OAuthProperties oauthProps;

    @Transactional
    public OAuthDtos.ExchangeResponse exchange(String code) {
        // 1) 用 code 换平台 token
        PlatformTokenResponse t = oauthClient.exchange(code);

        long expiresIn;
        try {
            expiresIn = Long.parseLong(t.getExpiresIn());
        } catch (Exception e) {
            expiresIn = 3600L;
        }

        // 2) 解析平台 access_token JWT payload,拿 providerUserId / email / displayName
        String providerUserId = null;
        String email = null;
        String displayName = null;
        try {
            Map<String, Object> claims = decodeJwtPayload(t.getAccessToken());
            Object uid = claims.get("userId");
            if (uid != null) providerUserId = uid.toString();
            Object em = claims.get("email");
            if (em != null) email = em.toString();
            Object nm = claims.get("displayName");
            if (nm == null) nm = claims.get("name");
            if (nm == null) nm = claims.get("username");
            if (nm != null) displayName = nm.toString();
        } catch (Exception e) {
            log.warn("解析 platform access_token payload 失败: {}", e.toString());
        }

        if (providerUserId == null || providerUserId.isBlank()) {
            throw new BusinessException(ErrorCode.OAUTH_EXCHANGE_FAILED, "无法从平台 Token 解析用户 ID");
        }

        // 3) 查找/创建本地 User
        User user = userService.findOrCreateByOAuth(
                oauthProps.getProviderName(), providerUserId, email, displayName);

        // 4) 创建 session
        String sessionKey = UUID.randomUUID().toString().replace("-", "");
        UserOAuthSession session = new UserOAuthSession();
        session.setSessionKey(sessionKey);
        session.setUser(user);
        session.setProvider(oauthProps.getProviderName());
        session.setProviderUserId(providerUserId);
        session.setAccessToken(t.getAccessToken());
        session.setRefreshToken(t.getRefreshToken());
        session.setExpiresAt(Instant.now().plusSeconds(expiresIn));
        session.setEmail(email);
        session.setDisplayName(displayName);
        sessionRepository.save(session);

        // 5) 签发本地 JWT
        String token = jwtUtil.generateToken(
                user.getUsername(), user.getId(),
                user.getAuthorities(), sessionKey, "OAUTH");

        return new OAuthDtos.ExchangeResponse(token, "Bearer", sessionKey, userDtoMapper.toDto(user));
    }

    @Transactional
    public OAuthDtos.ExchangeResponse refresh(String sessionKey) {
        UserOAuthSession session = sessionRepository.findBySessionKey(sessionKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.OAUTH_SESSION_NOT_FOUND));

        if (session.getRefreshToken() == null || session.getRefreshToken().isBlank()) {
            throw new BusinessException(ErrorCode.OAUTH_REFRESH_FAILED, "刷新令牌不存在,请重新登录");
        }

        // 刷新平台 token
        PlatformTokenResponse t = oauthClient.refresh(session.getRefreshToken());
        long expiresIn;
        try {
            expiresIn = Long.parseLong(t.getExpiresIn());
        } catch (Exception e) {
            expiresIn = 3600L;
        }

        session.setAccessToken(t.getAccessToken());
        session.setRefreshToken(t.getRefreshToken());
        session.setExpiresAt(Instant.now().plusSeconds(expiresIn));
        sessionRepository.save(session);

        User user = session.getUser();
        String token = jwtUtil.generateToken(
                user.getUsername(), user.getId(),
                user.getAuthorities(), sessionKey, "OAUTH");

        return new OAuthDtos.ExchangeResponse(token, "Bearer", sessionKey, userDtoMapper.toDto(user));
    }

    @Transactional
    public void logout(String sessionKey) {
        UserOAuthSession session = sessionRepository.findBySessionKey(sessionKey).orElse(null);
        if (session != null && session.getRefreshToken() != null) {
            try {
                oauthClient.revoke(session.getRefreshToken());
            } catch (Exception e) {
                log.warn("revoke platform refresh token failed: {}", e.getMessage());
            }
        }
        if (session != null) {
            sessionRepository.delete(session);
        }
    }

    public UserOAuthSession require(String sessionKey) {
        return sessionRepository.findBySessionKey(sessionKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.OAUTH_SESSION_NOT_FOUND));
    }

    /**
     * 不验签直接 base64 解 JWT payload —— 平台返回的 token 我们已经信任(来自 server-to-server 交换)
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> decodeJwtPayload(String jwt) throws Exception {
        if (jwt == null) return Map.of();
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) return Map.of();
        byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
        return objectMapper.readValue(new String(payload, StandardCharsets.UTF_8), Map.class);
    }
}
