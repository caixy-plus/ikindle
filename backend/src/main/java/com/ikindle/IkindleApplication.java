package com.ikindle;

import com.ikindle.config.JwtProperties;
import com.ikindle.config.OAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * iKindle 电子书阅读平台主启动类
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({JwtProperties.class, OAuthProperties.class})
public class IkindleApplication {

    public static void main(String[] args) {
        SpringApplication.run(IkindleApplication.class, args);
    }
} 