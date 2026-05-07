package com.ikindle.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String signature;
    private String phone;
    private Boolean phoneVerified;
    private String email;
    private Boolean emailVerified;
    private Boolean enabled;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Set<String> roles;
}