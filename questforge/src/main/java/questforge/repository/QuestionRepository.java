package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import questforge.model.Question;

import java.util.List;

public interface QuestionRepository
        extends JpaRepository<Question, Integer> {

    List<Question> findByChallengeId(
            Integer challengeId
    );

    @Modifying
    @Transactional
    void deleteByChallengeId(
            Integer challengeId
    );
}