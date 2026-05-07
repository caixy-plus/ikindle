package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.dto.LoginRequest;
import com.ikindle.dto.LoginResponse;
import com.ikindle.dto.RegisterRequest;
import com.ikindle.dto.UserDTO;
import com.ikindle.entity.User;
import com.ikindle.mapper.UserDtoMapper;
import com.ikindle.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@Valid @RequestBody RegisterRequest req) {
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setNickname(req.getNickname());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        return ApiResponse.success(userDtoMapper.toDto(userService.register(user)));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = userService.login(req.getUsername(), req.getPassword());
        User user = userService.findByUsername(req.getUsername()).orElseThrow();
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setTokenType("Bearer");
        resp.setUser(userDtoMapper.toDto(user));
        return ApiResponse.success(resp);
    }

    @GetMapping("/me")
    public ApiResponse<UserDTO> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return ApiResponse.unauthorized("未登录");
        }
        String username = auth.getPrincipal().toString();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "当前用户不存在"));
        return ApiResponse.success(userDtoMapper.toDto(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userService.findAll().stream()
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
        return ApiResponse.success(userDTOs);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userDtoMapper.toDto(userService.findByIdOrThrow(id)));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        checkOwnershipOrAdmin(id);
        userDTO.setId(id);
        User user = userDtoMapper.toEntity(userDTO);
        user.setId(id);
        User updated = userService.updateUserInfo(user);
        return ApiResponse.success(userDtoMapper.toDto(updated));
    }

    @PutMapping("/{id}/password")
    public ApiResponse<Void> changePassword(@PathVariable Long id,
                                            @RequestBody Map<String, String> body) {
        checkOwnershipOrAdmin(id);
        userService.changePassword(id, body.get("oldPassword"), body.get("newPassword"));
        return ApiResponse.success();
    }

    private void checkOwnershipOrAdmin(Long targetUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return;
        }
        String username = auth.getPrincipal().toString();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "当前用户不存在"));
        if (!currentUser.getId().equals(targetUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作其他用户");
        }
    }

    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ApiResponse.success();
    }
}
