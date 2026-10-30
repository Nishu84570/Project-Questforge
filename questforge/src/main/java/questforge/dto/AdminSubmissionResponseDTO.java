package questforge.dto;

import java.time.LocalDateTime;

public class AdminSubmissionResponseDTO {

    private Integer submissionId;

    private Integer userId;

    private String userName;

    private Integer challengeId;

    private String challengeTitle;

    private String answer;

    private String status;

    private LocalDateTime submittedAt;

    private Integer totalQuestions;

    private Integer correctAnswers;

    private Integer score;

    private Boolean firstSubmission;

    public AdminSubmissionResponseDTO() {
    }

    public AdminSubmissionResponseDTO(
            Integer submissionId,
            Integer userId,
            String userName,
            Integer challengeId,
            String challengeTitle,
            String answer,
            String status,
            LocalDateTime submittedAt,
            Integer totalQuestions,
            Integer correctAnswers,
            Integer score,
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

    public Integer getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Integer submissionId) {
        this.submissionId = submissionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
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

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getFirstSubmission() {
        return firstSubmission;
    }

    public void setFirstSubmission(Boolean firstSubmission) {
        this.firstSubmission = firstSubmission;
    }
}