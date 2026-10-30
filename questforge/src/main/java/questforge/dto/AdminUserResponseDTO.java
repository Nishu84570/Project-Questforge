package questforge.dto;

public class AdminUserResponseDTO {

    private Integer userId;
    private String name;
    private String email;
    private String role;
    private Integer xp;
    private Integer level;
    private long challengesAttempted;
    private long challengesCompleted;
    private int currentStreak;
    private int longestStreak;

    public AdminUserResponseDTO() {
    }

    public AdminUserResponseDTO(
            Integer userId,
            String name,
            String email,
            String role,
            Integer xp,
            Integer level,
            long challengesAttempted,
            long challengesCompleted,
            int currentStreak,
            int longestStreak) {

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public long getChallengesAttempted() {
        return challengesAttempted;
    }

    public void setChallengesAttempted(
            long challengesAttempted) {

        this.challengesAttempted =
                challengesAttempted;
    }

    public long getChallengesCompleted() {
        return challengesCompleted;
    }

    public void setChallengesCompleted(
            long challengesCompleted) {

        this.challengesCompleted =
                challengesCompleted;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(
            int currentStreak) {

        this.currentStreak =
                currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(
            int longestStreak) {

        this.longestStreak =
                longestStreak;
    }
}