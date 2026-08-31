package com.platform.auth.security.blacklist;

public interface TokenBlacklistService {

    /**
     * Blacklist an access token for its remaining validity duration in milliseconds.
     *
     * @param token           JWT Access Token string
     * @param remainingTtlMs  Time-to-live in milliseconds until the token expires naturally
     */
    void blacklistToken(String token, long remainingTtlMs);

    /**
     * Check if an access token has been revoked / blacklisted.
     *
     * @param token JWT Access Token string
     * @return true if token is in the blacklist, false otherwise
     */
    boolean isBlacklisted(String token);
}
