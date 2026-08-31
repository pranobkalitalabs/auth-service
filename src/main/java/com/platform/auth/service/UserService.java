package com.platform.auth.service;

import com.platform.auth.domain.enums.RoleType;
import com.platform.auth.dto.request.UpdateProfileRequest;
import com.platform.auth.dto.response.ApiResponse;
import com.platform.auth.dto.response.UserProfileResponse;
import com.platform.auth.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface UserService {
    UserProfileResponse getCurrentUserProfile(UserPrincipal currentUser);
    UserProfileResponse updateUserProfile(UserPrincipal currentUser, UpdateProfileRequest request);
    UserProfileResponse getUserById(UUID userId);
    Page<UserProfileResponse> getAllUsers(Pageable pageable);
    UserProfileResponse updateUserRoles(UUID userId, Set<RoleType> roleTypes);
    ApiResponse<Void> deleteUser(UUID userId);
}
