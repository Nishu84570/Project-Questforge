
package questforge.dto;

public class OptionResponseDTO {

    private Long id;
    private Long questionId;
    private String optionText;
    private Boolean isCorrect;

    public OptionResponseDTO() {
    }

    public OptionResponseDTO(
            Long id,
            Long questionId,
            String optionText,
            Boolean isCorrect) {

        this.id = id;
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
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

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
