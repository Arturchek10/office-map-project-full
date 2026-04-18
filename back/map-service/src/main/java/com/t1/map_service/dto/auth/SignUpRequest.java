package com.t1.map_service.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Please provide a valid email address")
    String email,
    @NotBlank(message = "Name is mandatory")
    String name,

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8)
    String password
) { }
