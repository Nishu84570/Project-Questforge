package questforge.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SubmissionRequestDTO {

    @NotNull(message = "Challenge ID is required")
    private Integer challengeId;

    @NotEmpty(message = "At least one answer is required")
    @Valid
    private List<SubmissionAnswerDTO> answers;

    public SubmissionRequestDTO() {
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }

    public List<SubmissionAnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(
            List<SubmissionAnswerDTO> answers) {

        this.answers = answers;
    }
}