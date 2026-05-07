package com.ikindle.service.impl;

import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.entity.Role;
import com.ikindle.entity.User;
import com.ikindle.entity.UserOAuthSession;
import com.ikindle.repository.RoleRepository;
import com.ikindle.repository.UserOAuthSessionRepository;
import com.ikindle.repository.UserRepository;
import com.ikindle.service.AccountService;
import com.ikindle.service.UserService;
import com.ikindle.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户Service实现类
 */
@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

    private final UserRepository userRepository;
    private final UserOAuthSessionRepository oauthSessionRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AccountService accountService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserOAuthSessionRepository oauthSessionRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           @Lazy AccountService accountService) {
        super(userRepository);
        this.userRepository = userRepository;
        this.oauthSessionRepository = oauthSessionRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.accountService = accountService;
    }

    @Override
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }
        if (user.getPhone() != null && userRepository.existsByPhone(user.getPhone())) {
            throw new BusinessException(ErrorCode.PHONE_EXISTS);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setPhoneVerified(false);
        user.setEmailVerified(false);

        User saved = userRepository.save(user);
        accountService.createForUser(saved.getId());
        return saved;
    }

    @Override
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.getEnabled()) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_INCORRECT);
        }
        return jwtUtil.generateToken(user.getUsername(), user.getId(),
                user.getAuthorities(), null, "LOCAL");
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public User updateUserInfo(User user) {
        User existingUser = findByIdOrThrow(user.getId());

        if (user.getNickname() != null) existingUser.setNickname(user.getNickname());
        if (user.getAvatarUrl() != null) existingUser.setAvatarUrl(user.getAvatarUrl());
        if (user.getSignature() != null) existingUser.setSignature(user.getSignature());
        if (user.getPhone() != null) existingUser.setPhone(user.getPhone());
        if (user.getEmail() != null) existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = findByIdOrThrow(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "邮箱不存在"));
        // TODO: 发送重置密码邮件
        throw new BusinessException(ErrorCode.INTERNAL_ERROR, "重置密码功能待实现");
    }

    @Override
    public void toggleUserStatus(Long userId) {
        User user = findByIdOrThrow(userId);
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }

    @Override
    public List<User> findByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        return userRepository.findByUsernameOrNicknameContaining(keyword);
    }

    @Override
    public Page<User> findUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        User user = findByIdOrThrow(userId);
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(permission));
    }

    @Override
    @Transactional
    public User findOrCreateByOAuth(String provider, String providerUserId, String email, String displayName) {
        // 先查找是否已有该 provider + providerUserId 的 session
        Optional<UserOAuthSession> existingSession =
                oauthSessionRepository.findByProviderAndProviderUserId(provider, providerUserId);
        if (existingSession.isPresent()) {
            User user = existingSession.get().getUser();
            // 更新用户信息(如果 displayName / email 有变化)
            boolean updated = false;
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
                updated = true;
            }
            if (displayName != null && !displayName.equals(user.getNickname())) {
                user.setNickname(displayName);
                updated = true;
            }
            if (updated) {
                userRepository.save(user);
            }
            return user;
        }

        // 首次 OAuth 登录:创建本地 User
        String username = generateOAuthUsername(provider, providerUserId);
        while (userRepository.existsByUsername(username)) {
            username = generateOAuthUsername(provider, providerUserId);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setNickname(displayName != null ? displayName : username);
        user.setEmail(email);
        user.setEnabled(true);
        user.setPhoneVerified(false);
        user.setEmailVerified(false);

        // 分配默认 ROLE_USER
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR, "默认角色 USER 不存在"));
        user.setRoles(new HashSet<>(List.of(userRole)));

        User saved = userRepository.save(user);
        accountService.createForUser(saved.getId());
        return saved;
    }

    private String generateOAuthUsername(String provider, String providerUserId) {
        String prefix = "oauth_" + provider.toLowerCase() + "_";
        String suffix = providerUserId.length() > 8
                ? providerUserId.substring(0, 8)
                : providerUserId;
        return prefix + suffix + "_" + UUID.randomUUID().toString().substring(0, 6);
    }
}
