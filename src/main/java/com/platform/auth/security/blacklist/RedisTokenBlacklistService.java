package com.platform.auth.security.blacklist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenBlacklistService implements TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(RedisTokenBlacklistService.class);
    private static final String BLACKLIST_KEY_PREFIX = "token:blacklist:";

    private final StringRedisTemplate redisTemplate;
    // In-memory fallback if Redis is unavailable
    private final ConcurrentHashMap<String, Long> inMemoryBlacklist = new ConcurrentHashMap<>();

    public RedisTokenBlacklistService(@Autowired(required = false) StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void blacklistToken(String token, long remainingTtlMs) {
        if (token == null || token.isBlank() || remainingTtlMs <= 0) {
            return;
        }

        long expirationTimestamp = System.currentTimeMillis() + remainingTtlMs;
        inMemoryBlacklist.put(token, expirationTimestamp);

        if (redisTemplate != null) {
            try {
                String key = BLACKLIST_KEY_PREFIX + token;
                redisTemplate.opsForValue().set(key, "revoked", Duration.ofMillis(remainingTtlMs));
                log.info("Blacklisted JWT access token in Redis for {} ms", remainingTtlMs);
            } catch (Exception ex) {
                log.warn("Redis unavailable for blacklisting. Stored in in-memory fallback cache: {}", ex.getMessage());
            }
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        // 1. Check in-memory fallback
        Long expiry = inMemoryBlacklist.get(token);
        if (expiry != null) {
            if (System.currentTimeMillis() < expiry) {
                return true;
            } else {
                inMemoryBlacklist.remove(token);
            }
        }

        // 2. Check Redis
        if (redisTemplate != null) {
            try {
                String key = BLACKLIST_KEY_PREFIX + token;
                Boolean exists = redisTemplate.hasKey(key);
                return Boolean.TRUE.equals(exists);
            } catch (Exception ex) {
                log.warn("Redis unavailable during blacklist check: {}", ex.getMessage());
            }
        }

        return false;
    }
}
