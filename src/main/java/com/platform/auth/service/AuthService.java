package com.platform.auth.service;

import com.platform.auth.dto.request.*;
import com.platform.auth.dto.response.ApiResponse;
import com.platform.auth.dto.response.AuthResponse;
import com.platform.auth.dto.response.TokenRefreshResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    TokenRefreshResponse refreshToken(RefreshTokenRequest request);
    ApiResponse<Void> logout(RefreshTokenRequest request);
    ApiResponse<String> requestPasswordReset(PasswordResetRequest request);
    ApiResponse<Void> resetPassword(ResetPasswordSubmitRequest request);
}
