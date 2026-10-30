
package questforge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AnswerRequestDTO {

    @NotNull(message = "Attempt ID is required")
    @Positive(message = "Attempt ID must be positive")
    private Long attemptId;

    @NotNull(message = "Question ID is required")
    @Positive(message = "Question ID must be positive")
    private Long questionId;

    @NotNull(message = "Option ID is required")
    @Positive(message = "Option ID must be positive")
    private Long optionId;

    public AnswerRequestDTO() {
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }
}
