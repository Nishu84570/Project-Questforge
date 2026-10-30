package questforge.dto;

import jakarta.validation.constraints.NotBlank;

public class ProfileUpdateRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    public ProfileUpdateRequestDTO() {
    }

    public ProfileUpdateRequestDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}