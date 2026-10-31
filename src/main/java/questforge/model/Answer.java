
package questforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Attempt ID is required")
    @Positive(message = "Attempt ID must be positive")
    @Column(name = "attempt_id", nullable = false)
    private Long attemptId;

    @NotNull(message = "Question ID is required")
    @Positive(message = "Question ID must be positive")
    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @NotNull(message = "Option ID is required")
    @Positive(message = "Option ID must be positive")
    @Column(name = "option_id", nullable = false)
    private Long optionId;

    public Answer() {
    }

    public Long getId() {
        return id;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }
}
