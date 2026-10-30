package questforge.dto;

import jakarta.validation.constraints.NotNull;

public class AttemptRequestDTO {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Challenge ID is required")
    private Integer challengeId;

    public AttemptRequestDTO() {}

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }
}