
package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import questforge.model.WeeklyQuest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyQuestRepository
        extends JpaRepository<WeeklyQuest, Long> {

    Optional<WeeklyQuest> findByUserIdAndWeekStart(Long userId , LocalDate weekStart);

    List<WeeklyQuest> findByUserIdOrderByWeekStartDesc(Long userId);
}
