package questforge.dto;

public class ChallengeAnalyticsResponseDTO {

    private Integer challengeId;

    private String title;

    private long totalSubmissions;

    private long firstSubmissions;

    private double averageScore;

    private double completionRate;

    public ChallengeAnalyticsResponseDTO() {
    }

    public ChallengeAnalyticsResponseDTO(
            Integer challengeId,
            String title,
            long totalSubmissions,
            long firstSubmissions,
            double averageScore,
            double completionRate) {

        this.challengeId = challengeId;

        this.title = title;

        this.totalSubmissions =
                totalSubmissions;

        this.firstSubmissions =
                firstSubmissions;

        this.averageScore =
                averageScore;

        this.completionRate =
                completionRate;
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(
            Integer challengeId) {

        this.challengeId =
                challengeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(
            String title) {

        this.title =
                title;
    }

    public long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(
            long totalSubmissions) {

        this.totalSubmissions =
                totalSubmissions;
    }

    public long getFirstSubmissions() {
        return firstSubmissions;
    }

    public void setFirstSubmissions(
            long firstSubmissions) {

        this.firstSubmissions =
                firstSubmissions;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(
            double averageScore) {

        this.averageScore =
                averageScore;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(
            double completionRate) {

        this.completionRate =
                completionRate;
    }
}