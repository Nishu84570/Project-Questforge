package questforge.dto;

public class AdminChallengeOverviewDTO {

    private Integer challengeId;

    private String title;

    private String difficulty;

    private String category;

    private long totalSubmissions;

    private long firstSubmissions;

    private long uniqueParticipants;

    private double averageScore;

    private Integer highestScore;

    private Integer lowestScore;

    private double completionRate;

    public AdminChallengeOverviewDTO() {
    }

    public AdminChallengeOverviewDTO(
            Integer challengeId,
            String title,
            String difficulty,
            String category,
            long totalSubmissions,
            long firstSubmissions,
            long uniqueParticipants,
            double averageScore,
            Integer highestScore,
            Integer lowestScore,
            double completionRate) {

        this.challengeId = challengeId;
        this.title = title;
        this.difficulty = difficulty;
        this.category = category;
        this.totalSubmissions = totalSubmissions;
        this.firstSubmissions = firstSubmissions;
        this.uniqueParticipants = uniqueParticipants;
        this.averageScore = averageScore;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
        this.completionRate = completionRate;
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public long getFirstSubmissions() {
        return firstSubmissions;
    }

    public void setFirstSubmissions(long firstSubmissions) {
        this.firstSubmissions = firstSubmissions;
    }

    public long getUniqueParticipants() {
        return uniqueParticipants;
    }

    public void setUniqueParticipants(long uniqueParticipants) {
        this.uniqueParticipants = uniqueParticipants;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Integer highestScore) {
        this.highestScore = highestScore;
    }

    public Integer getLowestScore() {
        return lowestScore;
    }

    public void setLowestScore(Integer lowestScore) {
        this.lowestScore = lowestScore;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }
}