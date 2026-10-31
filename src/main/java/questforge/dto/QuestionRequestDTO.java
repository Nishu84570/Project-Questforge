
package questforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class QuestionRequestDTO {

    @NotNull(message = "Challenge ID is required")
    @Positive(message = "Challenge ID must be positive")
    private Long challengeId;

    @NotBlank(message = "Question text is required")
    @Size(max = 2000, message = "Question text must not exceed 2000 characters")
    private String questionText;

    public QuestionRequestDTO() {
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
