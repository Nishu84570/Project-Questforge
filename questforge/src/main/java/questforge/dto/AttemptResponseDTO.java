package questforge.dto;

import java.time.LocalDateTime;

public class AttemptResponseDTO {

    private Integer id;
    private Integer userId;
    private Integer challengeId;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer score;

    public AttemptResponseDTO() {}

    public AttemptResponseDTO(
            Integer id,
            Integer userId,
            Integer challengeId,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            Integer score) {

        this.id = id;
        this.userId = userId;
        this.challengeId = challengeId;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.score = score;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}