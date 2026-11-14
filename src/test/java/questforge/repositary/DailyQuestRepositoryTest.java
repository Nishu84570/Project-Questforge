
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.DailyQuest;
import questforge.repository.DailyQuestRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DailyQuestRepositoryTest {

    @Autowired
    private DailyQuestRepository dailyQuestRepository;

    private DailyQuest createDailyQuest(
            Long userId,
            LocalDate questDate,
            String title) {

        DailyQuest quest = new DailyQuest();

        quest.setUserId(userId);
        quest.setQuestDate(questDate);
        quest.setTitle(title);
        quest.setTarget(10L);
        quest.setProgress(0L);
        quest.setCompleted(false);
        quest.setRewardXp(50L);

        return quest;
    }

    @Test
    void shouldSaveAndFindDailyQuestById() {

        LocalDate date = LocalDate.of(2026, 11, 13);

        DailyQuest saved =
                dailyQuestRepository.saveAndFlush(
                        createDailyQuest(1L, date, "Complete Java"));

        Optional<DailyQuest> result =
                dailyQuestRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1L);
        assertThat(result.get().getQuestDate()).isEqualTo(date);
        assertThat(result.get().getTitle()).isEqualTo("Complete Java");
    }

    @Test
    void shouldFindQuestByUserIdAndQuestDate() {

        LocalDate date = LocalDate.of(2026, 11, 13);

        dailyQuestRepository.save(
                createDailyQuest(1L, date, "Complete Java"));

        Optional<DailyQuest> result =
                dailyQuestRepository.findByUserIdAndQuestDate(
                        1L, date);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Complete Java");
    }

    @Test
    void shouldReturnEmptyWhenQuestDoesNotExistForUserAndDate() {

        LocalDate date = LocalDate.of(2026, 11, 13);

        Optional<DailyQuest> result =
                dailyQuestRepository.findByUserIdAndQuestDate(
                        99L, date);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindQuestsByUserIdInDescendingDateOrder() {

        LocalDate date1 = LocalDate.of(2026, 11, 10);
        LocalDate date2 = LocalDate.of(2026, 11, 11);
        LocalDate date3 = LocalDate.of(2026, 11, 12);

        dailyQuestRepository.save(
                createDailyQuest(1L, date1, "Quest One"));

        dailyQuestRepository.save(
                createDailyQuest(1L, date3, "Quest Three"));

        dailyQuestRepository.save(
                createDailyQuest(1L, date2, "Quest Two"));

        dailyQuestRepository.save(
                createDailyQuest(2L, date1, "Other User Quest"));

        List<DailyQuest> result =
                dailyQuestRepository.findByUserIdOrderByQuestDateDesc(1L);

        assertThat(result).hasSize(3);

        assertThat(result)
                .extracting(DailyQuest::getQuestDate)
                .containsExactly(date3, date2, date1);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoDailyQuests() {

        List<DailyQuest> result =
                dailyQuestRepository.findByUserIdOrderByQuestDateDesc(99L);

        assertThat(result).isEmpty();
    }
}
