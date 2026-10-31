
package questforge.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class SubmissionRequestDTO {

    @NotNull(message = "Challenge ID is required")
    @Positive(message = "Challenge ID must be positive")
    private Long challengeId;

    @NotEmpty(message = "At least one answer is required")
    @Valid
    private List<SubmissionAnswerDTO> answers;

    public SubmissionRequestDTO() {}

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public List<SubmissionAnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SubmissionAnswerDTO> answers) {
        this.answers = answers;
    }
}
