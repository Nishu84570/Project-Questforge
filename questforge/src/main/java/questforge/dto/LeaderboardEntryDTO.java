package questforge.dto;

import java.time.LocalDateTime;

public class LeaderboardEntryDTO {

    private Integer rank;

    private Integer userId;

    private String userName;

    private Integer challengeId;

    private String challengeTitle;

    private Integer score;

    private Integer correctAnswers;

    private Integer totalQuestions;

    private LocalDateTime submittedAt;

    public LeaderboardEntryDTO() {
    }

    public LeaderboardEntryDTO(
            Integer rank,
            Integer userId,
            String userName,
            Integer challengeId,
            String challengeTitle,
            Integer score,
            Integer correctAnswers,
            Integer totalQuestions,
            LocalDateTime submittedAt) {

        this.rank = rank;
        this.userId = userId;
        this.userName = userName;
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.score = score;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.submittedAt = submittedAt;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(
            LocalDateTime submittedAt) {

        this.submittedAt = submittedAt;
    }
}