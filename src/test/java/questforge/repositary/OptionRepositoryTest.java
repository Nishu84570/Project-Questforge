
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.Option;
import questforge.repository.OptionRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OptionRepositoryTest {

    @Autowired
    private OptionRepository optionRepository;

    private Option createOption(Long questionId, String text, Boolean correct) {
        Option option = new Option();
        option.setQuestionId(questionId);
        option.setOptionText(text);
        option.setIsCorrect(correct);
        return option;
    }

    @Test
    void shouldSaveAndFindOptionById() {
        Option saved = optionRepository.save(
                createOption(1L, "Java", true)
        );

        Option found = optionRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getOptionText()).isEqualTo("Java");
        assertThat(found.getQuestionId()).isEqualTo(1L);
        assertThat(found.getIsCorrect()).isTrue();
    }

    @Test
    void shouldFindOptionsByQuestionId() {
        optionRepository.save(createOption(1L, "Java", true));
        optionRepository.save(createOption(1L, "Python", false));
        optionRepository.save(createOption(2L, "C++", false));

        List<Option> result = optionRepository.findByQuestionId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Option::getOptionText)
                .containsExactlyInAnyOrder("Java", "Python");
    }

    @Test
    void shouldReturnEmptyListWhenQuestionHasNoOptions() {
        List<Option> result = optionRepository.findByQuestionId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteOptionsByQuestionId() {
        optionRepository.save(createOption(1L, "Java", true));
        optionRepository.save(createOption(1L, "Python", false));
        optionRepository.save(createOption(2L, "C++", false));

        int deleted = optionRepository.deleteByQuestionId(1L);

        assertThat(deleted).isEqualTo(2);
        assertThat(optionRepository.findByQuestionId(1L)).isEmpty();
    }

    @Test
    void shouldNotDeleteOptionsOfOtherQuestions() {
        optionRepository.save(createOption(1L, "Java", true));
        optionRepository.save(createOption(2L, "Python", false));

        optionRepository.deleteByQuestionId(1L);

        assertThat(optionRepository.findByQuestionId(2L)).hasSize(1);
    }
}
