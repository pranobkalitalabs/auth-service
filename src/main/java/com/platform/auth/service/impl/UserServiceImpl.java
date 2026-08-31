package com.platform.auth.service.impl;

import com.platform.auth.domain.entity.Address;
import com.platform.auth.domain.entity.Role;
import com.platform.auth.domain.entity.User;
import com.platform.auth.domain.enums.RoleType;
import com.platform.auth.dto.request.AddressDto;
import com.platform.auth.dto.request.UpdateProfileRequest;
import com.platform.auth.dto.response.ApiResponse;
import com.platform.auth.dto.response.UkAddressLookupResponse;
import com.platform.auth.dto.response.UserProfileResponse;
import com.platform.auth.exception.AppException;
import com.platform.auth.exception.ResourceNotFoundException;
import com.platform.auth.repository.PasswordResetTokenRepository;
import com.platform.auth.repository.RefreshTokenRepository;
import com.platform.auth.repository.RoleRepository;
import com.platform.auth.repository.UserRepository;
import com.platform.auth.security.UserPrincipal;
import com.platform.auth.service.UkAddressService;
import com.platform.auth.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UkAddressService ukAddressService;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           UkAddressService ukAddressService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.ukAddressService = ukAddressService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        return UserProfileResponse.fromEntity(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(UserPrincipal currentUser, UpdateProfileRequest request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhoneNumber(request.getPhoneNumber());

        if (request.getAddress() != null) {
            AddressDto addrDto = request.getAddress();
            Address address = user.getAddress() != null ? user.getAddress() : new Address();
            address.setAddressLine1(addrDto.getAddressLine1());
            address.setAddressLine2(addrDto.getAddressLine2());
            address.setCity(addrDto.getCity());
            address.setCounty(addrDto.getCounty());
            address.setPostcode(addrDto.getPostcode());
            address.setCountry(addrDto.getCountry() != null ? addrDto.getCountry() : "United Kingdom");

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

        User updatedUser = userRepository.save(user);
        return UserProfileResponse.fromEntity(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return UserProfileResponse.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserProfileResponse::fromEntity);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserRoles(UUID userId, Set<RoleType> roleTypes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Set<Role> roles = new HashSet<>();
        for (RoleType roleType : roleTypes) {
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new AppException("Role not found: " + roleType));
            roles.add(role);
        }

        user.setRoles(roles);
        User updated = userRepository.save(user);
        return UserProfileResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        passwordResetTokenRepository.deleteByUser(user);
        refreshTokenRepository.deleteByUser(user);
        userRepository.delete(user);
        return ApiResponse.success("User deleted successfully");
    }
}
