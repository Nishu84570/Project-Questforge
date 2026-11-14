
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.Achievement;
import questforge.repository.AchievementRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AchievementRepositoryTest {

    @Autowired
    private AchievementRepository achievementRepository;

    private Achievement createAchievement(
            Long userId, String code, String name) {

        Achievement achievement = new Achievement();
        achievement.setUserId(userId);
        achievement.setCode(code);
        achievement.setName(name);
        achievement.setDescription("Test achievement");
        achievement.setIcon("trophy");

        return achievement;
    }

    @Test
    void shouldSaveAndFindAchievementById() {

        Achievement achievement =
                createAchievement(1L, "FIRST_WIN", "First Win");

        Achievement saved =
                achievementRepository.saveAndFlush(achievement);

        Optional<Achievement> result =
                achievementRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("FIRST_WIN");
        assertThat(result.get().getUserId()).isEqualTo(1L);
    }

    @Test
    void shouldFindAchievementsByUserId() {

        achievementRepository.save(
                createAchievement(1L, "FIRST_WIN", "First Win"));

        achievementRepository.save(
                createAchievement(1L, "LEVEL_UP", "Level Up"));

        achievementRepository.save(
                createAchievement(2L, "FIRST_WIN", "First Win"));

        List<Achievement> result =
                achievementRepository.findByUserId(1L);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Achievement::getUserId)
                .containsOnly(1L);
    }

    @Test
    void shouldFindAchievementByUserIdAndCode() {

        achievementRepository.save(
                createAchievement(1L, "FIRST_WIN", "First Win"));

        Optional<Achievement> result =
                achievementRepository.findByUserIdAndCode(
                        1L, "FIRST_WIN");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("First Win");
    }

    @Test
    void shouldReturnEmptyWhenAchievementDoesNotExist() {

        Optional<Achievement> result =
                achievementRepository.findByUserIdAndCode(
                        99L, "UNKNOWN");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCheckWhetherAchievementExists() {

        achievementRepository.save(
                createAchievement(1L, "FIRST_WIN", "First Win"));

        boolean exists =
                achievementRepository.existsByUserIdAndCode(
                        1L, "FIRST_WIN");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenAchievementDoesNotExist() {

        boolean exists =
                achievementRepository.existsByUserIdAndCode(
                        99L, "UNKNOWN");

        assertThat(exists).isFalse();
    }
}
