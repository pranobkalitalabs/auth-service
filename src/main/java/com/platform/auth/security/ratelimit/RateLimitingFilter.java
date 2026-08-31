package com.platform.auth.security.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.auth.dto.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final RateLimitingService rateLimitingService;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(RateLimitingService rateLimitingService, ObjectMapper objectMapper) {
        this.rateLimitingService = rateLimitingService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Only enforce rate limits on sensitive auth endpoints
        if (uri.startsWith("/api/v1/auth/")) {
            String clientIp = extractClientIp(request);
            String endpointCategory = determineCategory(uri);

            if (!rateLimitingService.tryConsume(clientIp, endpointCategory)) {
                log.warn("Rate limit exceeded for IP: {} on URI: {}", clientIp, uri);

                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setHeader("Retry-After", "60");

                ApiResponse<Void> apiResponse = ApiResponse.error("Too many requests. Rate limit exceeded. Please try again later.");
                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String determineCategory(String uri) {
        if (uri.contains("/login")) return "login";
        if (uri.contains("/register")) return "register";
        if (uri.contains("/forgot-password") || uri.contains("/reset-password")) return "forgot-password";
        return "general";
    }

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
