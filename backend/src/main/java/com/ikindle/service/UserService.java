package com.ikindle.service;

import com.ikindle.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 用户Service接口
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
public interface UserService extends BaseService<User, Long> {

    /**
     * 用户注册
     */
    User register(User user);

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 更新用户信息
     */
    User updateUserInfo(User user);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    void resetPassword(String email);

    /**
     * 启用/禁用用户
     */
    void toggleUserStatus(Long userId);

    /**
     * 根据角色查找用户
     */
    List<User> findByRoleName(String roleName);

    /**
     * 根据关键词搜索用户
     */
    List<User> searchUsers(String keyword);

    /**
     * 分页查找用户
     */
    Page<User> findUsers(Pageable pageable);

    /**
     * 验证用户权限
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * OAuth 登录:根据 providerUserId 查找或创建本地 User
     * 首次登录自动创建用户,分配默认角色,创建账户
     */
    User findOrCreateByOAuth(String provider, String providerUserId, String email, String displayName);
} 