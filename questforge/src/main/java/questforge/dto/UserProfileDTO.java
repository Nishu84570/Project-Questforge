package questforge.dto;

public class UserProfileDTO {

    private Integer id;
    private String name;
    private String email;
    private String role;

    private Integer totalAttempts;
    private Integer firstAttempts;
    private Integer practiceAttempts;

    private Integer averageScore;
    private Integer bestScore;

    private Integer totalCorrectAnswers;
    private Integer totalQuestionsAttempted;

    private boolean profileImagePresent;
    private String profileImageUrl;

    public UserProfileDTO() {
    }

    public UserProfileDTO(
            Integer id,
            String name,
            String email,
            String role,
            Integer totalAttempts,
            Integer firstAttempts,
            Integer practiceAttempts,
            Integer averageScore,
            Integer bestScore,
            Integer totalCorrectAnswers,
            Integer totalQuestionsAttempted,
            boolean profileImagePresent,
            String profileImageUrl) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;

        this.totalAttempts = totalAttempts;
        this.firstAttempts = firstAttempts;
        this.practiceAttempts = practiceAttempts;

        this.averageScore = averageScore;
        this.bestScore = bestScore;

        this.totalCorrectAnswers =
                totalCorrectAnswers;

        this.totalQuestionsAttempted =
                totalQuestionsAttempted;

        this.profileImagePresent =
                profileImagePresent;

        this.profileImageUrl =
                profileImageUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(Integer totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public Integer getFirstAttempts() {
        return firstAttempts;
    }

    public void setFirstAttempts(Integer firstAttempts) {
        this.firstAttempts = firstAttempts;
    }

    public Integer getPracticeAttempts() {
        return practiceAttempts;
    }

    public void setPracticeAttempts(Integer practiceAttempts) {
        this.practiceAttempts = practiceAttempts;
    }

    public Integer getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Integer averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getBestScore() {
        return bestScore;
    }

    public void setBestScore(Integer bestScore) {
        this.bestScore = bestScore;
    }

    public Integer getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public void setTotalCorrectAnswers(
            Integer totalCorrectAnswers) {

        this.totalCorrectAnswers =
                totalCorrectAnswers;
    }

    public Integer getTotalQuestionsAttempted() {
        return totalQuestionsAttempted;
    }

    public void setTotalQuestionsAttempted(
            Integer totalQuestionsAttempted) {

        this.totalQuestionsAttempted =
                totalQuestionsAttempted;
    }

    public boolean isProfileImagePresent() {
        return profileImagePresent;
    }

    public void setProfileImagePresent(
            boolean profileImagePresent) {

        this.profileImagePresent =
                profileImagePresent;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(
            String profileImageUrl) {

        this.profileImageUrl =
                profileImageUrl;
    }
}