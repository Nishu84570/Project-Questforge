
package questforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminUserRoleRequestDTO {

    @NotBlank(message = "Role is required")
    @Size(max = 30, message = "Role cannot exceed 30 characters")
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
