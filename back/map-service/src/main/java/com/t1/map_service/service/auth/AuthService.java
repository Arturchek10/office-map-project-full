package com.t1.map_service.service.auth;

import com.t1.map_service.dto.auth.AuthResponse;
import com.t1.map_service.dto.auth.SignInRequest;
import com.t1.map_service.dto.auth.SignUpRequest;
import com.t1.map_service.exception.EntityAlreadyExistsException;
import com.t1.map_service.security.service.jwt.JwtService;
import com.t1.map_service.repository.UserRepository;
import com.t1.map_service.model.entity.User;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse signUp(SignUpRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()){
            throw new EntityAlreadyExistsException("Пользователь с таким email уже существует");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setName(request.name());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(
                savedUser.getId().toString(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole()
        );
        String refreshToken = jwtService.generateRefreshToken(savedUser.getId().toString());

        return new AuthResponse(accessToken, refreshToken);

    }

    public AuthResponse signIn(SignInRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Пользователь не найден"));

        boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPassword());

        if (!passwordMatches){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Неверный пароль");
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
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token is invalid or expired");
        }

        Claims claims = jwtService.extractAllClaims(refreshToken);
        String userId = claims.getSubject();

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Пользователь не найден"));

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