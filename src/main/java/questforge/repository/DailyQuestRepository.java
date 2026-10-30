
package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import questforge.model.DailyQuest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyQuestRepository
        extends JpaRepository<DailyQuest, Long> {

    Optional<DailyQuest> findByUserIdAndQuestDate(Long userId , LocalDate questDate);

    List<DailyQuest> findByUserIdOrderByQuestDateDesc(Long userId);
}
