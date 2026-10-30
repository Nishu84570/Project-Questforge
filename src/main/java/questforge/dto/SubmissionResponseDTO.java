
package questforge.dto;

import java.time.LocalDateTime;

public class SubmissionResponseDTO {

    private Long id;
    private Long userId;
    private Long challengeId;
    private String answer;
    private String status;
    private LocalDateTime submittedAt;
    private long totalQuestions;
    private long correctAnswers;
    private long score;
    private Boolean firstSubmission;

    public SubmissionResponseDTO() {
    }

    public SubmissionResponseDTO(
            Long id,
            Long userId,
            Long challengeId,
            String answer,
            String status,
            LocalDateTime submittedAt,
            long totalQuestions,
            long correctAnswers,
            long score,
            Boolean firstSubmission) {

        this.id = id;
        this.userId = userId;
        this.challengeId = challengeId;
        this.answer = answer;
        this.status = status;
        this.submittedAt = submittedAt;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.firstSubmission = firstSubmission;
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
