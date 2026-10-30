package questforge.dto;

public class AnswerResponseDTO {

    private Integer id;
    private Integer attemptId;
    private Integer questionId;
    private Integer optionId;

    public AnswerResponseDTO() {}

    public AnswerResponseDTO(
            Integer id,
            Integer attemptId,
            Integer questionId,
            Integer optionId) {

        this.id = id;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.optionId = optionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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