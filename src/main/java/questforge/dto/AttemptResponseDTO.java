
package questforge.dto;

import java.time.LocalDateTime;

public class AttemptResponseDTO {

    private Long id;
    private Long userId;
    private Long challengeId;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long score;

    public AttemptResponseDTO() {
    }

    public AttemptResponseDTO(
            Long id,
            Long userId,
            Long challengeId,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            Long score) {

        this.id = id;
        this.userId = userId;
        this.challengeId = challengeId;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
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

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
