package questforge.dto;

import jakarta.validation.constraints.NotNull;

public class SubmissionAnswerDTO {

    @NotNull(message = "Question ID is required")
    private Integer questionId;

    @NotNull(message = "Option ID is required")
    private Integer optionId;

    public SubmissionAnswerDTO() {
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