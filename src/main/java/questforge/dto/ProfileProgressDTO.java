
package questforge.dto;

import java.util.List;

public class ProfileProgressDTO {

    private Long userId;
    private String name;
    private String email;

    private long xp;
    private Integer level;

    private long nextLevelXp;
    private long xpToNextLevel;

    private long challengesAttempted;
    private long challengesCompleted;

    private Integer averageScore;
    private Integer globalRank;

    private List<AchievementResponseDTO> achievements;

    private long currentStreak;
    private long longestStreak;

    public ProfileProgressDTO() {
    }

    public ProfileProgressDTO(
            Long userId,
            String name,
            String email,
            long xp,
            Integer level,
            long nextLevelXp,
            long xpToNextLevel,
            long challengesAttempted,
            long challengesCompleted,
            Integer averageScore,
            Integer globalRank,
            List<AchievementResponseDTO> achievements,
            long currentStreak,
            long longestStreak) {

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public long getNextLevelXp() {
        return nextLevelXp;
    }

    public void setNextLevelXp(long nextLevelXp) {
        this.nextLevelXp = nextLevelXp;
    }

    public long getXpToNextLevel() {
        return xpToNextLevel;
    }

    public void setXpToNextLevel(long xpToNextLevel) {
        this.xpToNextLevel = xpToNextLevel;
    }

    public long getChallengesAttempted() {
        return challengesAttempted;
    }

    public void setChallengesAttempted(long challengesAttempted) {
        this.challengesAttempted = challengesAttempted;
    }

    public long getChallengesCompleted() {
        return challengesCompleted;
    }

    public void setChallengesCompleted(long challengesCompleted) {
        this.challengesCompleted = challengesCompleted;
    }

    public Integer getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Integer averageScore) {
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

    public long getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(long currentStreak) {
        this.currentStreak = currentStreak;
    }

    public long getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(long longestStreak) {
        this.longestStreak = longestStreak;
    }
}
