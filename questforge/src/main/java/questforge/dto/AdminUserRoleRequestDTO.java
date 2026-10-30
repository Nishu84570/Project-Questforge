package questforge.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminUserRoleRequestDTO {

    @NotBlank(message = "Role is required")
    private String role;

    public AdminUserRoleRequestDTO() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}