
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.WeeklyQuest;
import questforge.repository.WeeklyQuestRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WeeklyQuestRepositoryTest {

    @Autowired
    private WeeklyQuestRepository weeklyQuestRepository;

    private WeeklyQuest createQuest(Long userId, LocalDate weekStart) {
        WeeklyQuest quest = new WeeklyQuest();
        quest.setUserId(userId);
        quest.setWeekStart(weekStart);
        quest.setTitle("Complete Java Challenges");
        quest.setTarget(10L);
        quest.setProgress(3L);
        quest.setCompleted(false);
        quest.setRewardXp(100L);

        return quest;
    }

    @Test
    void shouldSaveAndFindWeeklyQuestById() {
        LocalDate date = LocalDate.of(2026, 1, 5);

        WeeklyQuest saved =
                weeklyQuestRepository.save(createQuest(1L, date));

        WeeklyQuest found =
                weeklyQuestRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getUserId()).isEqualTo(1L);
        assertThat(found.getWeekStart()).isEqualTo(date);
        assertThat(found.getTitle()).isEqualTo("Complete Java Challenges");
    }

    @Test
    void shouldFindQuestByUserIdAndWeekStart() {
        LocalDate date = LocalDate.of(2026, 1, 5);

        weeklyQuestRepository.save(createQuest(1L, date));

        var result = weeklyQuestRepository.findByUserIdAndWeekStart(1L, date);

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnEmptyWhenQuestDoesNotExistForWeek() {
        LocalDate date = LocalDate.of(2026, 1, 5);

        assertThat(
                weeklyQuestRepository.findByUserIdAndWeekStart(99L, date)
        ).isEmpty();
    }

    @Test
    void shouldFindUserQuestsInDescendingWeekOrder() {
        LocalDate firstWeek = LocalDate.of(2026, 1, 5);
        LocalDate secondWeek = LocalDate.of(2026, 1, 12);
        LocalDate thirdWeek = LocalDate.of(2026, 1, 19);

        weeklyQuestRepository.save(createQuest(1L, firstWeek));
        weeklyQuestRepository.save(createQuest(1L, thirdWeek));
        weeklyQuestRepository.save(createQuest(1L, secondWeek));

        List<WeeklyQuest> result =
                weeklyQuestRepository.findByUserIdOrderByWeekStartDesc(1L);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(WeeklyQuest::getWeekStart)
                .containsExactly(thirdWeek, secondWeek, firstWeek);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoWeeklyQuests() {
        assertThat(
                weeklyQuestRepository.findByUserIdOrderByWeekStartDesc(99L)
        ).isEmpty();
    }
}
