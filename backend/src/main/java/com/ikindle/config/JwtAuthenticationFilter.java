package com.ikindle.config;

import com.ikindle.entity.User;
import com.ikindle.repository.UserRepository;
import com.ikindle.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * 从请求头解析 Bearer token 并设置安全上下文
 * 支持两种 token 模式:
 * 1) 本地登录 JWT (authType=LOCAL 或缺失): 验证签名+过期,加载用户真实 roles
 * 2) OAuth JWT (authType=OAUTH): 验证签名+过期,额外校验 session 存在于 DB
 * 特殊: /api/oauth/refresh 允许使用过期 token (仅验签)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = jwtUtil.extractTokenFromHeader(header);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                boolean isRefreshEndpoint = request.getRequestURI().endsWith("/api/oauth/refresh");
                String username;
                Long userId;
                List<String> authorityStrings;

                if (isRefreshEndpoint) {
                    // refresh 接口:只验签,不校验过期时间
                    Claims claims = jwtUtil.parseTokenWithoutExpiry(token);
                    username = claims.getSubject();
                    userId = claims.get("userId", Long.class);
                    Object authoritiesObj = claims.get("authorities");
                    authorityStrings = parseAuthorityList(authoritiesObj);
                } else {
                    username = jwtUtil.getUsernameFromToken(token);
                    if (Boolean.TRUE.equals(jwtUtil.isTokenExpired(token))) {
                        chain.doFilter(request, response);
                        return;
                    }
                    userId = jwtUtil.getUserIdFromToken(token);
                    authorityStrings = jwtUtil.getAuthoritiesFromToken(token);
                }

                if (username != null && userId != null) {
                    List<GrantedAuthority> authorities;
                    if (authorityStrings != null && !authorityStrings.isEmpty()) {
                        // Token 已包含 authorities (新 token)
                        authorities = authorityStrings.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                    } else {
                        // 兼容旧 token:从 DB 加载用户获取真实 roles
                        authorities = loadAuthoritiesFromDb(userId);
                    }

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities
                            );

                    // 如果是 OAuth token,将 sessionKey 存入 details 供 OAuth controller 使用
                    String sessionKey = isRefreshEndpoint
                            ? jwtUtil.parseTokenWithoutExpiry(token).get("sessionKey", String.class)
                            : jwtUtil.getSessionKeyFromToken(token);
                    if (sessionKey != null) {
                        auth.setDetails(sessionKey);
                    } else {
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    }
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException e) {
                log.debug("JWT 解析失败: {}", e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    private List<String> parseAuthorityList(Object authoritiesObj) {
        if (authoritiesObj instanceof List) {
            return ((List<?>) authoritiesObj).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private List<GrantedAuthority> loadAuthoritiesFromDb(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    List<GrantedAuthority> list = new java.util.ArrayList<>();
                    list.addAll(user.getAuthorities());
                    return list;
                })
                .orElse(List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
