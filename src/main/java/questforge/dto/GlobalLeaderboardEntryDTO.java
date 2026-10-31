
package questforge.dto;

public class GlobalLeaderboardEntryDTO {

    private Integer rank;
    private Long userId;
    private String userName;
    private long xp;
    private Integer level;

    public GlobalLeaderboardEntryDTO() {
    }

    public GlobalLeaderboardEntryDTO(
            Integer rank,
            Long userId,
            String userName,
            long xp,
            Integer level) {

        this.rank = rank;
        this.userId = userId;
        this.userName = userName;
        this.xp = xp;
        this.level = level;
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
}
