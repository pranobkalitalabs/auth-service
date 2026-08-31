package com.platform.auth.service.impl;

import com.platform.auth.config.JwtProperties;
import com.platform.auth.domain.entity.*;
import com.platform.auth.domain.enums.AuthProvider;
import com.platform.auth.domain.enums.RoleType;
import com.platform.auth.dto.request.*;
import com.platform.auth.dto.response.*;
import com.platform.auth.exception.AppException;
import com.platform.auth.exception.BadRequestException;
import com.platform.auth.exception.ResourceNotFoundException;
import com.platform.auth.exception.UnauthorizedException;
import com.platform.auth.repository.PasswordResetTokenRepository;
import com.platform.auth.repository.RoleRepository;
import com.platform.auth.repository.UserRepository;
import com.platform.auth.security.JwtTokenProvider;
import com.platform.auth.security.UserPrincipal;
import com.platform.auth.security.blacklist.TokenBlacklistService;
import com.platform.auth.service.AuthService;
import com.platform.auth.service.RefreshTokenService;
import com.platform.auth.service.UkAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UkAddressService ukAddressService;
    private final JwtProperties jwtProperties;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider,
                           RefreshTokenService refreshTokenService,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           UkAddressService ukAddressService,
                           JwtProperties jwtProperties,
                           TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.ukAddressService = ukAddressService;
        this.jwtProperties = jwtProperties;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BadRequestException("Email address already in use: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setProvider(AuthProvider.LOCAL);
        user.setEnabled(true);
        user.setEmailVerified(false);

        // Process UK Address if supplied
        if (request.getAddress() != null) {
            AddressDto addrDto = request.getAddress();
            Address address = new Address();
            address.setAddressLine1(addrDto.getAddressLine1());
            address.setAddressLine2(addrDto.getAddressLine2());
            address.setCity(addrDto.getCity());
            address.setCounty(addrDto.getCounty());
            address.setPostcode(addrDto.getPostcode());
            address.setCountry(addrDto.getCountry() != null ? addrDto.getCountry() : "United Kingdom");

            // Look up geo & administrative information if postcode is provided
            if (addrDto.getPostcode() != null && !addrDto.getPostcode().isBlank()) {
                UkAddressLookupResponse lookup = ukAddressService.lookupPostcode(addrDto.getPostcode());
                if (lookup.isValid()) {
                    address.setLatitude(lookup.getLatitude());
                    address.setLongitude(lookup.getLongitude());
                    if (address.getCounty() == null || address.getCounty().isBlank()) {
                        address.setCounty(lookup.getRegion() != null ? lookup.getRegion() : lookup.getAdminDistrict());
                    }
                }
            }
            user.setAddress(address);
        }

        // Assign Default Role ROLE_USER
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new AppException("Default User Role not set in system"));
        user.setRoles(Collections.singleton(userRole));

        User savedUser = userRepository.save(user);

        UserPrincipal principal = UserPrincipal.create(savedUser);
        String accessToken = tokenProvider.generateAccessToken(principal);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                jwtProperties.getAccessTokenExpirationMs(),
                UserSummaryDto.fromEntity(savedUser)
        );
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        String accessToken = tokenProvider.generateAccessToken(principal);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                jwtProperties.getAccessTokenExpirationMs(),
                UserSummaryDto.fromEntity(user)
        );
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserPrincipal principal = UserPrincipal.create(user);
                    String newAccessToken = tokenProvider.generateAccessToken(principal);
                    return new TokenRefreshResponse(
                            newAccessToken,
                            requestRefreshToken,
                            jwtProperties.getAccessTokenExpirationMs()
                    );
                })
                .orElseThrow(() -> new UnauthorizedException("Refresh token is not in database"));
    }

    @Override
    @Transactional
    public ApiResponse<Void> logout(RefreshTokenRequest request) {
        return logout(request, null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> logout(RefreshTokenRequest request, String bearerToken) {
        if (request != null && request.getRefreshToken() != null) {
            refreshTokenService.findByToken(request.getRefreshToken())
                    .ifPresent(refreshTokenService::deleteToken);
        }

        if (bearerToken != null && !bearerToken.isBlank()) {
            String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;
            long remainingTtl = tokenProvider.getRemainingExpirationMs(token);
            if (remainingTtl > 0) {
                tokenBlacklistService.blacklistToken(token, remainingTtl);
            }
        }

        return ApiResponse.success("Log out successful");
    }

    @Override
    @Transactional
    public ApiResponse<String> requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // Remove any existing password reset token
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                user,
                token,
                Instant.now().plus(24, ChronoUnit.HOURS)
        );
        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset token generated for {}: {}", user.getEmail(), token);
        return ApiResponse.success("Password reset instructions sent. Token (for dev): " + token, token);
    }

    @Override
    @Transactional
    public ApiResponse<Void> resetPassword(ResetPasswordSubmitRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token"));

        if (resetToken.isExpired() || resetToken.isUsed()) {
            throw new BadRequestException("Password reset token has expired or has already been used");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        return ApiResponse.success("Password has been reset successfully. You can now log in.");
    }
}
