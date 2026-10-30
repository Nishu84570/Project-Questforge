package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import questforge.model.Attempt;

import java.util.List;

public interface AttemptRepository
        extends JpaRepository<Attempt, Integer> {

    List<Attempt> findByUserId(Integer userId);

    List<Attempt> findByChallengeId(Integer challengeId);
}