package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import questforge.model.WeeklyQuest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyQuestRepository
        extends JpaRepository<WeeklyQuest, Integer> {

    Optional<WeeklyQuest> findByUserIdAndWeekStart(
            Integer userId,
            LocalDate weekStart
    );

    List<WeeklyQuest> findByUserIdOrderByWeekStartDesc(
            Integer userId
    );
}