package com.t1.map_service.dto.auth;

public record AuthResponse (
        String token,
        String refreshToken
) {
}
