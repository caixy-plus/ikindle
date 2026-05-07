package com.ikindle.auth.oauth;

import com.ikindle.dto.UserDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OAuthDtos {

    @Data
    public static class ExchangeRequest {
        @NotBlank(message = "授权码不能为空")
        private String code;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExchangeResponse {
        private String token;
        private String tokenType = "Bearer";
        private String sessionKey;
        private UserDTO user;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MeResponse {
        private String sessionKey;
        private String providerUserId;
        private String email;
        private String displayName;
    }
}
