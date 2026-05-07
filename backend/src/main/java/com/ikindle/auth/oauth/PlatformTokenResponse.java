package com.ikindle.auth.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 平台 OAuth Token 响应 DTO
 * 对齐 brain-think 的 TokenResponse
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String expiresIn;
}
