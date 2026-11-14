
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.UserStreak;
import questforge.repository.UserStreakRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserStreakRepositoryTest {

    @Autowired
    private UserStreakRepository userStreakRepository;

    private UserStreak createStreak(Long userId) {
        UserStreak streak = new UserStreak();
        streak.setUserId(userId);
        streak.setCurrentStreak(5L);
        streak.setLongestStreak(10L);
        streak.setLastActiveDate(LocalDate.now());

        return streak;
    }

    @Test
    void shouldSaveAndFindStreakById() {
        UserStreak saved = userStreakRepository.save(createStreak(1L));

        UserStreak found =
                userStreakRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getUserId()).isEqualTo(1L);
        assertThat(found.getCurrentStreak()).isEqualTo(5L);
        assertThat(found.getLongestStreak()).isEqualTo(10L);
    }

    @Test
    void shouldFindStreakByUserId() {
        userStreakRepository.save(createStreak(1L));

        var result = userStreakRepository.findByUserId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoStreak() {
        assertThat(userStreakRepository.findByUserId(99L)).isEmpty();
    }

    @Test
    void shouldUpdateStreakValues() {
        UserStreak saved = userStreakRepository.save(createStreak(1L));

        saved.setCurrentStreak(8L);
        saved.setLongestStreak(12L);

        userStreakRepository.save(saved);

        UserStreak updated =
                userStreakRepository.findByUserId(1L).orElseThrow();

        assertThat(updated.getCurrentStreak()).isEqualTo(8L);
        assertThat(updated.getLongestStreak()).isEqualTo(12L);
    }
}
