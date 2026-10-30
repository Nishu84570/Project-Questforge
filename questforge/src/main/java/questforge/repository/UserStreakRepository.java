package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import questforge.model.UserStreak;

import java.util.Optional;

public interface UserStreakRepository
        extends JpaRepository<UserStreak, Integer> {

    Optional<UserStreak> findByUserId(Integer userId);
}