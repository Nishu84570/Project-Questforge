
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.Answer;
import questforge.repository.AnswerRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AnswerRepositoryTest {

    @Autowired
    private AnswerRepository answerRepository;

    private Answer createAnswer(
        Long attemptId,
        Long questionId) {

    Answer answer = new Answer();

    answer.setAttemptId(attemptId);
    answer.setQuestionId(questionId);
    answer.setOptionId(101L);

    return answer;
}

    @Test
    void shouldSaveAndFindAnswerById() {

        Answer answer = createAnswer(1L, 101L);

        Answer saved = answerRepository.saveAndFlush(answer);

        var result = answerRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAttemptId()).isEqualTo(1L);
        assertThat(result.get().getQuestionId()).isEqualTo(101L);
    }

    @Test
    void shouldFindAnswersByAttemptId() {

        answerRepository.save(createAnswer(1L, 101L));
        answerRepository.save(createAnswer(1L, 102L));
        answerRepository.save(createAnswer(2L, 103L));

        List<Answer> result =
                answerRepository.findByAttemptId(1L);

        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(Answer::getAttemptId)
                .containsOnly(1L);
    }

    @Test
    void shouldReturnEmptyListWhenAttemptIdDoesNotExist() {

        List<Answer> result =
                answerRepository.findByAttemptId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenAnswerExistsForAttemptAndQuestion() {

        answerRepository.save(
                createAnswer(1L, 101L));

        boolean exists =
                answerRepository.existsByAttemptIdAndQuestionId(
                        1L, 101L);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenAnswerDoesNotExist() {

        boolean exists =
                answerRepository.existsByAttemptIdAndQuestionId(
                        99L, 999L);

        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnFalseWhenQuestionIdDoesNotMatch() {

        answerRepository.save(
                createAnswer(1L, 101L));

        boolean exists =
                answerRepository.existsByAttemptIdAndQuestionId(
                        1L, 102L);

        assertThat(exists).isFalse();
    }
}
