
package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import questforge.model.Attempt;

import java.util.List;

@Repository
public interface AttemptRepository
        extends JpaRepository<Attempt, Long> {

    List<Attempt> findByUserId(Long userId);

    List<Attempt> findByChallengeId(Long challengeId);
}
