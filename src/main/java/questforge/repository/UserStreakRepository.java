
package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import questforge.model.UserStreak;

import java.util.Optional;

@Repository
public interface UserStreakRepository
        extends JpaRepository<UserStreak, Long> {

    Optional<UserStreak> findByUserId(Long userId);
}
