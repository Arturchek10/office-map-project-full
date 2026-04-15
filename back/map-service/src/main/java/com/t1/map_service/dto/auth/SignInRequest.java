package com.t1.map_service.dto.auth;

public record SignInRequest (
    String login,
    String password
){

}
