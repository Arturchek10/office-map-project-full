package com.t1.map_service.dto.auth;

public record SignInRequest (
    String email,
    String password
){

}
