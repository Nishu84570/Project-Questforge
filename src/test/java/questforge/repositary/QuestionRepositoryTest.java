
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.Question;
import questforge.repository.QuestionRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    private Question createQuestion(Long challengeId, String text) {
        Question question = new Question();
        question.setChallengeId(challengeId);
        question.setQuestionText(text);
        return question;
    }

    @Test
    void shouldSaveAndFindQuestionById() {
        Question saved = questionRepository.save(
                createQuestion(1L, "What is Java?")
        );

        Question found = questionRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getQuestionText()).isEqualTo("What is Java?");
        assertThat(found.getChallengeId()).isEqualTo(1L);
    }

    @Test
    void shouldFindQuestionsByChallengeId() {
        questionRepository.save(createQuestion(1L, "Question 1"));
        questionRepository.save(createQuestion(1L, "Question 2"));
        questionRepository.save(createQuestion(2L, "Question 3"));

        List<Question> result = questionRepository.findByChallengeId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Question::getQuestionText)
                .containsExactlyInAnyOrder("Question 1", "Question 2");
    }

    @Test
    void shouldReturnEmptyListWhenChallengeHasNoQuestions() {
        List<Question> result = questionRepository.findByChallengeId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteQuestionsByChallengeId() {
        questionRepository.save(createQuestion(1L, "Question 1"));
        questionRepository.save(createQuestion(1L, "Question 2"));
        questionRepository.save(createQuestion(2L, "Question 3"));

        int deleted = questionRepository.deleteByChallengeId(1L);

        assertThat(deleted).isEqualTo(2);
        assertThat(questionRepository.findByChallengeId(1L)).isEmpty();
    }

    @Test
    void shouldNotDeleteQuestionsOfOtherChallenges() {
        questionRepository.save(createQuestion(1L, "Question 1"));
        questionRepository.save(createQuestion(2L, "Question 2"));

        questionRepository.deleteByChallengeId(1L);

        assertThat(questionRepository.findByChallengeId(2L)).hasSize(1);
    }
}
