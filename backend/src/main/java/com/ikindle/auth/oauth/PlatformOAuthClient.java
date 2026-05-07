package com.ikindle.auth.oauth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.config.OAuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 平台 OAuth 客户端
 * 对齐 brain-think 的 PlatformOAuthClient
 * 调用上游平台的 OAuth 端点: token exchange / refresh / revoke
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlatformOAuthClient {

    private final RestClient platformRestClient;
    private final OAuthProperties props;
    private final ObjectMapper objectMapper;

    public PlatformTokenResponse exchange(String code) {
        ensureConfigured();
        Map<String, String> body = new HashMap<>();
        body.put("clientId", props.getClientId());
        body.put("clientSecret", props.getClientSecret());
        body.put("code", code);
        body.put("grantType", "authorization_code");

        return postForToken(props.getTokenUrl(), body);
    }

    public PlatformTokenResponse refresh(String refreshToken) {
        ensureConfigured();
        Map<String, String> body = new HashMap<>();
        body.put("clientId", props.getClientId());
        body.put("clientSecret", props.getClientSecret());
        body.put("refreshToken", refreshToken);

        return postForToken(props.getRefreshUrl(), body);
    }

    public void revoke(String refreshToken) {
        if (!props.isEnabled()) {
            return;
        }
        Map<String, String> body = new HashMap<>();
        body.put("clientId", props.getClientId());
        body.put("clientSecret", props.getClientSecret());
        body.put("token", refreshToken);

        try {
            platformRestClient.post()
                    .uri(props.getRevokeUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("revoke platform refresh token failed: {}", e.getMessage());
        }
    }

    private void ensureConfigured() {
        if (!props.isEnabled()) {
            throw new BusinessException(ErrorCode.OAUTH_NOT_CONFIGURED);
        }
        if (props.getClientId() == null || props.getClientId().isBlank()) {
            throw new BusinessException(ErrorCode.OAUTH_NOT_CONFIGURED, "OAuth clientId 未配置");
        }
    }

    private PlatformTokenResponse postForToken(String uri, Object body) {
        log.info("platform OAuth request: POST {}{} body={}", props.getBaseUrl(), uri, body);
        String raw = platformRestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
        log.info("platform OAuth response: {}", raw);
        try {
            if (raw == null || raw.isBlank()) {
                throw new BusinessException(ErrorCode.OAUTH_PLATFORM_ERROR, "平台返回空响应");
            }
            // 平台返回包装格式: {code, message, data: {...}}
            java.util.Map<String, Object> wrapper = objectMapper.readValue(raw,
                    new TypeReference<java.util.Map<String, Object>>() {});
            Object code = wrapper.get("code");
            if (code == null || !"0".equals(code.toString())) {
                Object msg = wrapper.getOrDefault("message", wrapper.get("msg"));
                throw new BusinessException(ErrorCode.OAUTH_PLATFORM_ERROR,
                        "平台返回错误: " + (msg != null ? msg : raw));
            }
            Object data = wrapper.get("data");
            if (data == null) {
                throw new BusinessException(ErrorCode.OAUTH_PLATFORM_ERROR, "平台响应缺少 data 字段");
            }
            return objectMapper.convertValue(data, PlatformTokenResponse.class);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.error("parse platform token response fail: {}", raw, e);
            throw new BusinessException(ErrorCode.OAUTH_PLATFORM_ERROR, "解析平台响应失败");
        }
    }
}
