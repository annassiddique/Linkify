package com.nyx.Linkify.feature.authentication.dto;

public class AuthenticateResponseBody {
    private String token;
    private String message;

    public AuthenticateResponseBody(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }



}
