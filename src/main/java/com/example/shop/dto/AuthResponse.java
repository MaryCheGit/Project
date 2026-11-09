package com.example.shop.dto;

import com.example.shop.enums.Role;

public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String fullName;
    private Long userId;
    private Role role;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String email, String fullName, Long userId, Role role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.userId = userId;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}