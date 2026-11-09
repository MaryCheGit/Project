package com.example.shop.dto;

public class LoginRequest {
    private String username;
    private String password;

    // ========== ГЕТТЕРЫ И СЕТТЕРЫ ==========

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
