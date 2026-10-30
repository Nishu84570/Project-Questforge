package questforge.dto;

public class OptionResponseDTO {

    private Integer id;
    private Integer questionId;
    private String optionText;
    private Boolean isCorrect;

    public OptionResponseDTO(
            Integer id,
            Integer questionId,
            String optionText,
            Boolean isCorrect) {

        this.id = id;
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    public Integer getId() {
        return id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }
}