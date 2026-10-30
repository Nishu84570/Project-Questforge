package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import questforge.model.Achievement;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository
        extends JpaRepository<Achievement, Integer> {

    // Get all achievements unlocked by a user
    List<Achievement> findByUserId(Integer userId);

    // Check whether a particular badge is already unlocked
    Optional<Achievement> findByUserIdAndCode(
            Integer userId,
            String code
    );

    // Check quickly whether achievement exists
    boolean existsByUserIdAndCode(
            Integer userId,
            String code
    );
}