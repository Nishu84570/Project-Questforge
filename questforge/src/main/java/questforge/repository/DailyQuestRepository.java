package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import questforge.model.DailyQuest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyQuestRepository
        extends JpaRepository<DailyQuest, Integer> {

    Optional<DailyQuest> findByUserIdAndQuestDate(
            Integer userId,
            LocalDate questDate
    );

    List<DailyQuest> findByUserIdOrderByQuestDateDesc(
            Integer userId
    );
}