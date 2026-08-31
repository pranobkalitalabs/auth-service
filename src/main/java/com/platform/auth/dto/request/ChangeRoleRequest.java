package com.platform.auth.dto.request;

import com.platform.auth.domain.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

@Schema(description = "Assign Roles to User (Admin Only)")
public class ChangeRoleRequest {

    @NotEmpty(message = "At least one role must be specified")
    @Schema(example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<RoleType> roles;

    public ChangeRoleRequest() {
    }

    public ChangeRoleRequest(Set<RoleType> roles) {
        this.roles = roles;
    }

    public Set<RoleType> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleType> roles) {
        this.roles = roles;
    }
}
