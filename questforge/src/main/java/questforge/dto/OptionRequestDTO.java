package questforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OptionRequestDTO {

    @NotNull(message = "Question ID is required")
    private Integer questionId;

    @NotBlank(message = "Option text is required")
    private String optionText;

    @NotNull(message = "Is correct is required")
    private Boolean isCorrect;

    public OptionRequestDTO() {
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }
}