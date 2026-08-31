package com.platform.auth.security.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    @Value("${app.rate-limiting.enabled:true}")
    private boolean enabled;

    @Value("${app.rate-limiting.login-per-minute:10}")
    private int loginPerMinute;

    @Value("${app.rate-limiting.register-per-minute:5}")
    private int registerPerMinute;

    @Value("${app.rate-limiting.forgot-password-per-minute:3}")
    private int forgotPasswordPerMinute;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public boolean tryConsume(String clientIp, String endpoint) {
        if (!enabled) {
            return true;
        }

        String key = clientIp + ":" + endpoint;
        Bucket bucket = buckets.computeIfAbsent(key, k -> createNewBucket(endpoint));
        return bucket.tryConsume(1);
    }

    public void resetLimits() {
        buckets.clear();
    }

    private Bucket createNewBucket(String endpoint) {
        int capacity;
        if (endpoint.contains("login")) {
            capacity = loginPerMinute;
        } else if (endpoint.contains("register")) {
            capacity = registerPerMinute;
        } else if (endpoint.contains("forgot-password") || endpoint.contains("reset-password")) {
            capacity = forgotPasswordPerMinute;
        } else {
            capacity = 60;
        }

        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(capacity, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
