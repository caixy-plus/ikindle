package com.ikindle.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ikindle.jwt")
public class JwtProperties {
    private String secret;
    private long expiration = 86400000;

    @PostConstruct
    public void validate() {
        if (secret == null || secret.getBytes().length < 32) {
            throw new IllegalStateException("ikindle.jwt.secret 长度必须 >= 32 bytes");
        }
    }
}
