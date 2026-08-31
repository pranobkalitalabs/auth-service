package com.platform.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication Response containing JWT Tokens")
public class AuthResponse {

    @Schema(example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;

    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;

    @Schema(example = "Bearer", defaultValue = "Bearer")
    private String tokenType = "Bearer";

    @Schema(example = "900000")
    private long expiresInMs;

    private UserSummaryDto user;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String refreshToken, long expiresInMs, UserSummaryDto user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresInMs = expiresInMs;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }

    public UserSummaryDto getUser() {
        return user;
    }

    public void setUser(UserSummaryDto user) {
        this.user = user;
    }
}
