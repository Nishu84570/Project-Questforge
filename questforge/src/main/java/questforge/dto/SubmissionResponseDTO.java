package questforge.dto;

import java.time.LocalDateTime;

public class SubmissionResponseDTO {

    private Integer id;

    private Integer userId;

    private Integer challengeId;

    private String answer;

    private String status;

    private LocalDateTime submittedAt;

    private Integer totalQuestions;

    private Integer correctAnswers;

    private Integer score;

    private Boolean firstSubmission;

    public SubmissionResponseDTO() {
    }

    public SubmissionResponseDTO(
            Integer id,
            Integer userId,
            Integer challengeId,
            String answer,
            String status,
            LocalDateTime submittedAt,
            Integer totalQuestions,
            Integer correctAnswers,
            Integer score,
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

    public void setSubmittedAt(
            LocalDateTime submittedAt) {

        this.submittedAt = submittedAt;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(
            Integer totalQuestions) {

        this.totalQuestions = totalQuestions;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(
            Integer correctAnswers) {

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

    public void setFirstSubmission(
            Boolean firstSubmission) {

        this.firstSubmission = firstSubmission;
    }
}