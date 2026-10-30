
package questforge.dto;

import java.time.LocalDateTime;

public class AdminSubmissionResponseDTO {

    private Long submissionId;
    private Long userId;
    private String userName;
    private Long challengeId;
    private String challengeTitle;
    private String answer;
    private String status;
    private LocalDateTime submittedAt;
    private Long totalQuestions;
    private Long correctAnswers;
    private Long score;
    private Boolean firstSubmission;

    public AdminSubmissionResponseDTO() {}

    public AdminSubmissionResponseDTO(
            Long submissionId,
            Long userId,
            String userName,
            Long challengeId,
            String challengeTitle,
            String answer,
            String status,
            LocalDateTime submittedAt,
            Long totalQuestions,
            Long correctAnswers,
            Long score,
            Boolean firstSubmission) {

        this.submissionId = submissionId;
        this.userId = userId;
        this.userName = userName;
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.answer = answer;
        this.status = status;
        this.submittedAt = submittedAt;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.firstSubmission = firstSubmission;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
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

    public Long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Long getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Long correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Boolean getFirstSubmission() {
        return firstSubmission;
    }

    public void setFirstSubmission(Boolean firstSubmission) {
        this.firstSubmission = firstSubmission;
    }
}
