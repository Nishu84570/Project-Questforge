
package questforge.dto;

import java.time.LocalDateTime;

public class LeaderboardEntryDTO {

    private Integer rank;
    private Long userId;
    private String userName;
    private Long challengeId;
    private String challengeTitle;
    private long score;
    private long correctAnswers;
    private long totalQuestions;
    private LocalDateTime submittedAt;

    public LeaderboardEntryDTO() {
    }

    public LeaderboardEntryDTO(
            Integer rank,
            Long userId,
            String userName,
            Long challengeId,
            String challengeTitle,
            long score,
            long correctAnswers,
            long totalQuestions,
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

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(long correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
