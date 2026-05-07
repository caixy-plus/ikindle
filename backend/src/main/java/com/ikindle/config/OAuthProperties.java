package com.ikindle.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ikindle.oauth")
public class OAuthProperties {
    private boolean enabled = false;
    private String providerName = "app_plat";
    private String baseUrl = "https://api.local.caixy.xin";
    private String clientId;
    private String clientSecret;
    private String authorizeUrl = "/api/v1/oauth/authorize";
    private String tokenUrl = "/api/v1/oauth/token";
    private String refreshUrl = "/api/v1/oauth/refresh";
    private String revokeUrl = "/api/v1/oauth/revoke";
    private String userinfoUrl = "/api/v1/oauth/userinfo";
}
