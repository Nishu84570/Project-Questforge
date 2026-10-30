package questforge.dto;

public class QuestionResponseDTO {

    private Integer id;
    private Integer challengeId;
    private String questionText;

    public QuestionResponseDTO() {
    }

    public QuestionResponseDTO(
            Integer id,
            Integer challengeId,
            String questionText) {

        this.id = id;
        this.challengeId = challengeId;
        this.questionText = questionText;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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