package questforge.dto;

import java.util.List;

public class ProfileProgressDTO {

    private Integer userId;
    private String name;
    private String email;

    private Integer xp;
    private Integer level;

    private Integer nextLevelXp;
    private Integer xpToNextLevel;

    private Integer challengesAttempted;
    private Integer challengesCompleted;

    private Integer averageScore;
    private Integer globalRank;

    private List<AchievementResponseDTO> achievements;

    private Integer currentStreak;
    private Integer longestStreak;

    public ProfileProgressDTO() {
    }

    public ProfileProgressDTO(
            Integer userId,
            String name,
            String email,
            Integer xp,
            Integer level,
            Integer nextLevelXp,
            Integer xpToNextLevel,
            Integer challengesAttempted,
            Integer challengesCompleted,
            Integer averageScore,
            Integer globalRank,
            List<AchievementResponseDTO> achievements,
            Integer currentStreak,
            Integer longestStreak) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.xp = xp;
        this.level = level;
        this.nextLevelXp = nextLevelXp;
        this.xpToNextLevel = xpToNextLevel;
        this.challengesAttempted = challengesAttempted;
        this.challengesCompleted = challengesCompleted;
        this.averageScore = averageScore;
        this.globalRank = globalRank;
        this.achievements = achievements;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getNextLevelXp() {
        return nextLevelXp;
    }

    public void setNextLevelXp(Integer nextLevelXp) {
        this.nextLevelXp = nextLevelXp;
    }

    public Integer getXpToNextLevel() {
        return xpToNextLevel;
    }

    public void setXpToNextLevel(Integer xpToNextLevel) {
        this.xpToNextLevel = xpToNextLevel;
    }

    public Integer getChallengesAttempted() {
        return challengesAttempted;
    }

    public void setChallengesAttempted(
            Integer challengesAttempted) {

        this.challengesAttempted =
                challengesAttempted;
    }

    public Integer getChallengesCompleted() {
        return challengesCompleted;
    }

    public void setChallengesCompleted(
            Integer challengesCompleted) {

        this.challengesCompleted =
                challengesCompleted;
    }

    public Integer getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(
            Integer averageScore) {

        this.averageScore = averageScore;
    }

    public Integer getGlobalRank() {
        return globalRank;
    }

    public void setGlobalRank(Integer globalRank) {
        this.globalRank = globalRank;
    }

    public List<AchievementResponseDTO> getAchievements() {
        return achievements;
    }

    public void setAchievements(
            List<AchievementResponseDTO> achievements) {

        this.achievements = achievements;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(
            Integer currentStreak) {

        this.currentStreak = currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(
            Integer longestStreak) {

        this.longestStreak = longestStreak;
    }
}