
package questforge.dto;

public class AdminUserResponseDTO {

    private Long userId;
    private String name;
    private String email;
    private String role;
    private Long xp;
    private Integer level;
    private long challengesAttempted;
    private long challengesCompleted;
    private long currentStreak;
    private long longestStreak;

    public AdminUserResponseDTO() {}

    public AdminUserResponseDTO(
            Long userId,
            String name,
            String email,
            String role,
            Long xp,
            Integer level,
            long challengesAttempted,
            long challengesCompleted,
            long currentStreak,
            long longestStreak) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.xp = xp;
        this.level = level;
        this.challengesAttempted = challengesAttempted;
        this.challengesCompleted = challengesCompleted;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getXp() {
        return xp;
    }

    public void setXp(Long xp) {
        this.xp = xp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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
