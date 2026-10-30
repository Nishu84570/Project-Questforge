
package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import questforge.model.Challenge;

@Repository
public interface ChallengeRepository
        extends JpaRepository<Challenge, Long> {

}
