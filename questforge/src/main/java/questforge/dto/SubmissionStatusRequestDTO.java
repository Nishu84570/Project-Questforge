package questforge.dto;

import jakarta.validation.constraints.NotBlank;

public class SubmissionStatusRequestDTO {

    @NotBlank(message = "Status is required")
    private String status;

    public SubmissionStatusRequestDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}