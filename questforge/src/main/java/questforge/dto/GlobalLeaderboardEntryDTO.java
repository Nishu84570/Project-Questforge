package questforge.dto;

public class GlobalLeaderboardEntryDTO {

    private Integer rank;

    private Integer userId;

    private String userName;

    private Integer xp;

    private Integer level;

    public GlobalLeaderboardEntryDTO() {
    }

    public GlobalLeaderboardEntryDTO(
            Integer rank,
            Integer userId,
            String userName,
            Integer xp,
            Integer level) {

        this.rank = rank;
        this.userId = userId;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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