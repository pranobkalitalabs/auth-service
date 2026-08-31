package com.platform.auth.service;

import com.platform.auth.domain.entity.RefreshToken;
import com.platform.auth.domain.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void revokeAllUserTokens(User user);
    void deleteToken(RefreshToken token);
}
