package questforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuestionRequestDTO {

    @NotNull(message = "Challenge ID is required")
    private Integer challengeId;

    @NotBlank(message = "Question text is required")
    private String questionText;

    public QuestionRequestDTO() {
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}