
package questforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Question ID is required")
    @Positive(message = "Question ID must be positive")
    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @NotBlank(message = "Option text is required")
    @Size(max = 500, message = "Option text cannot exceed 500 characters")
    @Column(name = "option_text", nullable = false, length = 500)
    private String optionText;

    @NotNull(message = "Is correct is required")
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    public Option() {
    }

    public Long getId() {
        return id;
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

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
