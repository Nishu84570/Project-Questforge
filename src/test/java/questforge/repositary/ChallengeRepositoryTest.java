
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.Challenge;
import questforge.repository.ChallengeRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ChallengeRepositoryTest {

    @Autowired
    private ChallengeRepository challengeRepository;

    private Challenge createChallenge(String title) {

        Challenge challenge = new Challenge();

        challenge.setTitle(title);
        challenge.setDescription("Test challenge description");
        challenge.setDifficulty("EASY");
        challenge.setCategory("JAVA");

        return challenge;
    }

    @Test
    void shouldSaveAndFindChallengeById() {

        Challenge challenge =
                createChallenge("Java Basics");

        Challenge saved =
                challengeRepository.saveAndFlush(challenge);

        Optional<Challenge> result =
                challengeRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Java Basics");
        assertThat(result.get().getDifficulty()).isEqualTo("EASY");
        assertThat(result.get().getCategory()).isEqualTo("JAVA");
    }

    @Test
    void shouldFindAllChallenges() {

        challengeRepository.save(createChallenge("Java Basics"));
        challengeRepository.save(createChallenge("Spring Boot"));
        challengeRepository.save(createChallenge("Data Structures"));

        List<Challenge> result =
                challengeRepository.findAll();

        assertThat(result).hasSize(3);
    }

    @Test
    void shouldReturnEmptyWhenChallengeDoesNotExist() {

        Optional<Challenge> result =
                challengeRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteChallenge() {

        Challenge saved =
                challengeRepository.saveAndFlush(
                        createChallenge("Java Basics"));

        challengeRepository.deleteById(saved.getId());

        Optional<Challenge> result =
                challengeRepository.findById(saved.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCheckWhetherChallengeExists() {

        Challenge saved =
                challengeRepository.saveAndFlush(
                        createChallenge("Spring Boot"));

        boolean exists =
                challengeRepository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }
}
