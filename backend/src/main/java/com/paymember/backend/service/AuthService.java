package com.paymember.backend.service;

import com.paymember.backend.dto.AuthDtos.AuthResponse;
import com.paymember.backend.dto.AuthDtos.GoogleLoginRequest;
import com.paymember.backend.dto.AuthDtos.LoginRequest;
import com.paymember.backend.dto.AuthDtos.RegisterRequest;
import com.paymember.backend.model.AppUser;
import com.paymember.backend.repository.AppUserRepository;
import com.paymember.backend.security.GoogleTokenVerifierService;
import com.paymember.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    public AuthService(
        AppUserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        GoogleTokenVerifierService googleTokenVerifierService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleTokenVerifierService = googleTokenVerifierService;
    }

    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already registered");
        });
        AppUser user = new AppUser();
        user.setEmail(request.email().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setProvider("local");
        AppUser saved = userRepository.save(user);
        return buildResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmail(request.email().toLowerCase().trim())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return buildResponse(user);
    }

    public AuthResponse googleLogin(GoogleLoginRequest request) {
        var principal = googleTokenVerifierService.verify(request.idToken());
        AppUser user = userRepository.findByEmail(principal.email().toLowerCase().trim())
            .orElseGet(() -> {
                AppUser nu = new AppUser();
                nu.setEmail(principal.email().toLowerCase().trim());
                nu.setPasswordHash(passwordEncoder.encode("google:" + principal.subject()));
                nu.setProvider("google");
                return userRepository.save(nu);
            });
        return buildResponse(user);
    }

    private AuthResponse buildResponse(AppUser user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail());
    }
}
