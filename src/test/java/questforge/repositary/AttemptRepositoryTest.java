
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.Attempt;
import questforge.repository.AttemptRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AttemptRepositoryTest {

    @Autowired
    private AttemptRepository attemptRepository;

    private Attempt createAttempt(Long userId, Long challengeId) {

        Attempt attempt = new Attempt();

        attempt.setUserId(userId);
        attempt.setChallengeId(challengeId);
        attempt.setScore(100L);

        return attempt;
    }

    @Test
    void shouldSaveAndFindAttemptById() {

        Attempt attempt = createAttempt(1L, 101L);

        Attempt saved = attemptRepository.saveAndFlush(attempt);

        Optional<Attempt> result =
                attemptRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1L);
        assertThat(result.get().getChallengeId()).isEqualTo(101L);
        assertThat(result.get().getScore()).isEqualTo(100L);
    }

    @Test
    void shouldFindAttemptsByUserId() {

        attemptRepository.save(createAttempt(1L, 101L));
        attemptRepository.save(createAttempt(1L, 102L));
        attemptRepository.save(createAttempt(2L, 101L));

        List<Attempt> result =
                attemptRepository.findByUserId(1L);

        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(Attempt::getUserId)
                .containsOnly(1L);
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoAttempts() {

        List<Attempt> result =
                attemptRepository.findByUserId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAttemptsByChallengeId() {

        attemptRepository.save(createAttempt(1L, 101L));
        attemptRepository.save(createAttempt(2L, 101L));
        attemptRepository.save(createAttempt(3L, 102L));

        List<Attempt> result =
                attemptRepository.findByChallengeId(101L);

        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(Attempt::getChallengeId)
                .containsOnly(101L);
    }

    @Test
    void shouldReturnEmptyWhenChallengeHasNoAttempts() {

        List<Attempt> result =
                attemptRepository.findByChallengeId(999L);

        assertThat(result).isEmpty();
    }
}
