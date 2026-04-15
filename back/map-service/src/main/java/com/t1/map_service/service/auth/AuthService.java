package com.t1.map_service.service.auth;

import com.t1.map_service.dto.auth.AuthResponse;
import com.t1.map_service.dto.auth.SignInRequest;
import com.t1.map_service.security.service.jwt.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    public AuthResponse signIn(SignInRequest request) {
        // Временная авторизация для запуска проекта
        if (!"test".equals(request.login()) || !"test".equals(request.password())) {
            throw new RuntimeException("Неверный логин или пароль");
        }

        String userId = "1";
        String email = "test@example.com";
        String name = "Test User";
        String role = "USER";

        String accessToken = jwtService.generateAccessToken(userId, email, name, role);
        String refreshToken = jwtService.generateRefreshToken(userId);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Refresh token is invalid or expired");
        }

        Claims claims = jwtService.extractAllClaims(refreshToken);
        String userId = claims.getSubject();

        // Пока восстанавливаем данные пользователя вручную
        // Позже это можно брать из БД
        String email = "test@example.com";
        String name = "Test User";
        String role = "USER";

        String newAccessToken = jwtService.generateAccessToken(userId, email, name, role);
        String newRefreshToken = jwtService.generateRefreshToken(userId);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}