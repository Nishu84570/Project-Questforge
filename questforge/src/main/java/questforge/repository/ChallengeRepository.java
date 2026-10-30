package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import questforge.model.Challenge;

public interface ChallengeRepository
        extends JpaRepository<Challenge, Integer> {

}