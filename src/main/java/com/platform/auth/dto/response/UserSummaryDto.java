package com.platform.auth.dto.response;

import com.platform.auth.domain.entity.User;
import com.platform.auth.domain.enums.AuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(description = "User Summary Details")
public class UserSummaryDto {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private AuthProvider provider;
    private Set<String> roles;

    public UserSummaryDto() {
    }

    public UserSummaryDto(UUID id, String email, String firstName, String lastName, AuthProvider provider, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.provider = provider;
        this.roles = roles;
    }

    public static UserSummaryDto fromEntity(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return new UserSummaryDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProvider(),
                roleNames
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
