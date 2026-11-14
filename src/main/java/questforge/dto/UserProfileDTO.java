
package questforge.dto;

import questforge.model.User.Role;

public class UserProfileDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;

    private long totalAttempts;
    private long firstAttempts;
    private long practiceAttempts;

    private long averageScore;
    private long bestScore;

    private long totalCorrectAnswers;
    private long totalQuestionsAttempted;

    private boolean profileImagePresent;
    private String profileImageUrl;

    public UserProfileDTO() {}

    public UserProfileDTO(
            Long id,
            String name,
            String email,
            Role role,
            long totalAttempts,
            long firstAttempts,
            long practiceAttempts,
            long averageScore,
            long bestScore,
            long totalCorrectAnswers,
            long totalQuestionsAttempted,
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
        this.totalCorrectAnswers = totalCorrectAnswers;
        this.totalQuestionsAttempted = totalQuestionsAttempted;
        this.profileImagePresent = profileImagePresent;
        this.profileImageUrl = profileImageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public long getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(long totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public long getFirstAttempts() {
        return firstAttempts;
    }

    public void setFirstAttempts(long firstAttempts) {
        this.firstAttempts = firstAttempts;
    }

    public long getPracticeAttempts() {
        return practiceAttempts;
    }

    public void setPracticeAttempts(long practiceAttempts) {
        this.practiceAttempts = practiceAttempts;
    }

    public long getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(long averageScore) {
        this.averageScore = averageScore;
    }

    public long getBestScore() {
        return bestScore;
    }

    public void setBestScore(long bestScore) {
        this.bestScore = bestScore;
    }

    public long getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public void setTotalCorrectAnswers(long totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }

    public long getTotalQuestionsAttempted() {
        return totalQuestionsAttempted;
    }

    public void setTotalQuestionsAttempted(long totalQuestionsAttempted) {
        this.totalQuestionsAttempted = totalQuestionsAttempted;
    }

    public boolean isProfileImagePresent() {
        return profileImagePresent;
    }

    public void setProfileImagePresent(boolean profileImagePresent) {
        this.profileImagePresent = profileImagePresent;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
