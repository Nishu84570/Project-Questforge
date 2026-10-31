
package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import questforge.model.Achievement;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository
        extends JpaRepository<Achievement, Long> {

    List<Achievement> findByUserId(Long userId);

    Optional<Achievement> findByUserIdAndCode(Long userId , String code);

    boolean existsByUserIdAndCode(Long userId , String code);
}
