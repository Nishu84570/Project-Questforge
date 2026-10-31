
package questforge.dto;

public class LeaderboardResponseDTO {

    private Integer rank;
    private Long userId;
    private String name;
    private String email;
    private long xp;
    private Integer level;

    public LeaderboardResponseDTO() {
    }

    public LeaderboardResponseDTO(
            Integer rank,
            Long userId,
            String name,
            String email,
            long xp,
            Integer level) {

        this.rank = rank;
        this.userId = userId;
        this.name = name;
        this.email = email;
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
}
