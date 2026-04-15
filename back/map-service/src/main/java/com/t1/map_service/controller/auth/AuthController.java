package com.t1.map_service.controller.auth;

import com.t1.map_service.dto.auth.AuthResponse;
import com.t1.map_service.dto.auth.SignInRequest;
import com.t1.map_service.dto.auth.RefreshTokenRequest;
import com.t1.map_service.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public AuthResponse signIn(@RequestBody SignInRequest request) {
        return authService.signIn(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.refreshToken());
    }
}
