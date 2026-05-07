package com.ikindle.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT工具类
 *
 * @author iKindle Team
 * @version 1.0.0
 */
@Component
public class JwtUtil {

    @Value("${ikindle.jwt.secret}")
    private String secret;

    @Value("${ikindle.jwt.expiration}")
    private Long expiration;

    /**
     * 生成JWT token (本地登录,不带 authorities)
     */
    public String generateToken(String username, Long userId) {
        return generateToken(username, userId, null, null, null);
    }

    /**
     * 生成JWT token (带 authorities 和可选 OAuth sessionKey)
     */
    public String generateToken(String username, Long userId,
                                 Collection<? extends GrantedAuthority> authorities,
                                 String sessionKey, String authType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        if (authorities != null && !authorities.isEmpty()) {
            List<String> authorityList = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            claims.put("authorities", authorityList);
        }
        if (sessionKey != null) {
            claims.put("sessionKey", sessionKey);
        }
        if (authType != null) {
            claims.put("authType", authType);
        }
        return createToken(claims, username);
    }

    /**
     * 创建token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从token中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从token中获取指定声明
     */
    public <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从token中获取所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 检查token是否过期
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 从token中获取权限列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object authorities = claims.get("authorities");
        if (authorities instanceof List) {
            return (List<String>) authorities;
        }
        return List.of();
    }

    /**
     * 从token中获取sessionKey (OAuth 登录时存在)
     */
    public String getSessionKeyFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("sessionKey", String.class);
    }

    /**
     * 从token中获取authType (LOCAL / OAUTH)
     */
    public String getAuthTypeFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        String authType = claims.get("authType", String.class);
        return authType != null ? authType : "LOCAL";
    }

    /**
     * 只验签,不校验过期时间 —— 供 refresh 接口使用
     * 捕获 ExpiredJwtException 并从异常中提取已验证的 Claims
     */
    public Claims parseTokenWithoutExpiry(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 签名已验证,仅过期 —— 返回 Claims 供 refresh 使用
            return e.getClaims();
        } catch (Exception e) {
            throw new JwtException("无效的 Token: " + e.getMessage());
        }
    }

    /**
     * 验证token
     */
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 从请求头中提取token
     */
    public String extractTokenFromHeader(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
} 