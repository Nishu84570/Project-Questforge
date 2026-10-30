
package questforge.dto;

public class QuestionResponseDTO {

    private Long id;
    private Long challengeId;
    private String questionText;

    public QuestionResponseDTO() {
    }

    public QuestionResponseDTO(
            Long id,
            Long challengeId,
            String questionText) {

        this.id = id;
        this.challengeId = challengeId;
        this.questionText = questionText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
