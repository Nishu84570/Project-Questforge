package questforge.dto;

public class LeaderboardResponseDTO {

    private Integer rank;

    private Integer userId;

    private String name;

    private String email;

    private Integer xp;

    private Integer level;

    // =========================
    // CONSTRUCTOR
    // =========================

    public LeaderboardResponseDTO() {
    }

    public LeaderboardResponseDTO(
            Integer rank,
            Integer userId,
            String name,
            String email,
            Integer xp,
            Integer level) {

        this.rank = rank;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.xp = xp;
        this.level = level;
    }

    // =========================
    // GETTERS AND SETTERS
    // =========================

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
}