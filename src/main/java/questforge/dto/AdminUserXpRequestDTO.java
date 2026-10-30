
package questforge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdminUserXpRequestDTO {

    @NotNull(message = "XP is required")
    @Min(value = 1, message = "XP must be greater than 0")
    private Long xp;

    public AdminUserXpRequestDTO() {
    }

    public Long getXp() {
        return xp;
    }

    public void setXp(Long xp) {
        this.xp = xp;
    }
}
