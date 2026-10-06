package com.ridelink.dto.response;

public class LoginResponse {

    private String token;

    private String tokenType;

    private long expiresIn;

    private UserResponse user;

    public LoginResponse(
            String token,
            long expiresIn,
            UserResponse user) {

        this.token = token;

        this.tokenType = "Bearer";

        this.expiresIn = expiresIn;

        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public UserResponse getUser() {
        return user;
    }
}