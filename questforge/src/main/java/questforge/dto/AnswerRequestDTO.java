package questforge.dto;

import jakarta.validation.constraints.NotNull;

public class AnswerRequestDTO {

    @NotNull(message = "Attempt ID is required")
    private Integer attemptId;

    @NotNull(message = "Question ID is required")
    private Integer questionId;

    @NotNull(message = "Option ID is required")
    private Integer optionId;

    public AnswerRequestDTO() {}

    public Integer getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Integer attemptId) {
        this.attemptId = attemptId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }
}