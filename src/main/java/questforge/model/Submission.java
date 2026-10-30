
package questforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Challenge ID is required")
    @Positive(message = "Challenge ID must be positive")
    @Column(name = "challenge_id", nullable = false)
    private Long challengeId;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Size(max = 50, message = "Status cannot exceed 50 characters")
    @Column(length = 50)
    private String status;

    @NotNull(message = "Submission timestamp is required")
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @PositiveOrZero(message = "Total questions cannot be negative")
    @Column(name = "total_questions", nullable = false)
    private long totalQuestions = 0L;

    @PositiveOrZero(message = "Correct answers cannot be negative")
    @Column(name = "correct_answers", nullable = false)
    private long correctAnswers = 0L;

    @PositiveOrZero(message = "Score cannot be negative")
    @Column(nullable = false)
    private long score = 0L;

    @NotNull(message = "First submission status is required")
    @Column(name = "first_submission", nullable = false)
    private Boolean firstSubmission = false;

    public Submission() {
    }

    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }

        if (firstSubmission == null) {
            firstSubmission = false;
        }
    }

    public Long getId() {
        return id;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(long correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public Boolean getFirstSubmission() {
        return firstSubmission;
    }

    public void setFirstSubmission(Boolean firstSubmission) {
        this.firstSubmission = firstSubmission;
    }
}
