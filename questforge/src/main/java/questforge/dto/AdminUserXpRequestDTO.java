package questforge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdminUserXpRequestDTO {

    @NotNull(message = "XP is required")
    @Min(value = 1, message = "XP must be greater than 0")
    private Integer xp;

    public AdminUserXpRequestDTO() {
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }
}