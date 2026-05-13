package com.paymember.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
    public record RegisterRequest(@Email String email, @NotBlank String password) {}
    public record LoginRequest(@Email String email, @NotBlank String password) {}
    public record GoogleLoginRequest(@NotBlank String idToken) {}
    public record AuthResponse(String token, Long userId, String email) {}
}
