package questforge.dto;

public class AdminDashboardResponseDTO {

    private long totalUsers;

    private long totalChallenges;

    private long totalSubmissions;

    private long totalAchievements;

    private long totalNotifications;

    private long totalXp;

    private double averageUserXp;

    private double averageSubmissionScore;

    private TopUserDTO topUser;

    private MostActiveChallengeDTO mostActiveChallenge;

    public AdminDashboardResponseDTO() {
    }

    public AdminDashboardResponseDTO(
            long totalUsers,
            long totalChallenges,
            long totalSubmissions,
            long totalAchievements,
            long totalNotifications,
            long totalXp,
            double averageUserXp,
            double averageSubmissionScore,
            TopUserDTO topUser,
            MostActiveChallengeDTO mostActiveChallenge) {

        this.totalUsers = totalUsers;

        this.totalChallenges =
                totalChallenges;

        this.totalSubmissions =
                totalSubmissions;

        this.totalAchievements =
                totalAchievements;

        this.totalNotifications =
                totalNotifications;

        this.totalXp =
                totalXp;

        this.averageUserXp =
                averageUserXp;

        this.averageSubmissionScore =
                averageSubmissionScore;

        this.topUser =
                topUser;

        this.mostActiveChallenge =
                mostActiveChallenge;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalChallenges() {
        return totalChallenges;
    }

    public void setTotalChallenges(long totalChallenges) {
        this.totalChallenges = totalChallenges;
    }

    public long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public long getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(long totalAchievements) {
        this.totalAchievements = totalAchievements;
    }

    public long getTotalNotifications() {
        return totalNotifications;
    }

    public void setTotalNotifications(long totalNotifications) {
        this.totalNotifications = totalNotifications;
    }

    public long getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(long totalXp) {
        this.totalXp = totalXp;
    }

    public double getAverageUserXp() {
        return averageUserXp;
    }

    public void setAverageUserXp(
            double averageUserXp) {

        this.averageUserXp =
                averageUserXp;
    }

    public double getAverageSubmissionScore() {
        return averageSubmissionScore;
    }

    public void setAverageSubmissionScore(
            double averageSubmissionScore) {

        this.averageSubmissionScore =
                averageSubmissionScore;
    }

    public TopUserDTO getTopUser() {
        return topUser;
    }

    public void setTopUser(
            TopUserDTO topUser) {

        this.topUser =
                topUser;
    }

    public MostActiveChallengeDTO
    getMostActiveChallenge() {

        return mostActiveChallenge;
    }

    public void setMostActiveChallenge(
            MostActiveChallengeDTO mostActiveChallenge) {

        this.mostActiveChallenge =
                mostActiveChallenge;
    }

    public static class TopUserDTO {

        private Integer userId;

        private String name;

        private Integer xp;

        public TopUserDTO() {
        }

        public TopUserDTO(
                Integer userId,
                String name,
                Integer xp) {

            this.userId = userId;

            this.name = name;

            this.xp = xp;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(
                Integer userId) {

            this.userId =
                    userId;
        }

        public String getName() {
            return name;
        }

        public void setName(
                String name) {

            this.name =
                    name;
        }

        public Integer getXp() {
            return xp;
        }

        public void setXp(Integer xp) {
            this.xp =
                    xp;
        }
    }

    public static class MostActiveChallengeDTO {

        private Integer challengeId;

        private String title;

        private long submissions;

        public MostActiveChallengeDTO() {
        }

        public MostActiveChallengeDTO(
                Integer challengeId,
                String title,
                long submissions) {

            this.challengeId =
                    challengeId;

            this.title =
                    title;

            this.submissions =
                    submissions;
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

        public long getSubmissions() {
            return submissions;
        }

        public void setSubmissions(
                long submissions) {

            this.submissions =
                    submissions;
        }
    }
}