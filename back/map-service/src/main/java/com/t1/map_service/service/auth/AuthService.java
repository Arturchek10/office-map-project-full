package com.t1.map_service.service.auth;

import com.t1.map_service.dto.auth.AuthResponse;
import com.t1.map_service.dto.auth.SignInRequest;
import com.t1.map_service.security.service.jwt.JwtService;
import com.t1.map_service.repository.UserRepository;
import com.t1.map_service.model.entity.User;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse signIn(SignInRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPassword());

        if (!passwordMatches){
            throw new RuntimeException("Неверный логин или пароль");
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId().toString(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
        String refreshToken = jwtService.generateRefreshToken(user.getId().toString());
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Refresh token is invalid or expired");
        }

        Claims claims = jwtService.extractAllClaims(refreshToken);
        String userId = claims.getSubject();

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String newAccessToken = jwtService.generateAccessToken(
                user.getId().toString(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
        String newRefreshToken = jwtService.generateRefreshToken(user.getId().toString());

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}