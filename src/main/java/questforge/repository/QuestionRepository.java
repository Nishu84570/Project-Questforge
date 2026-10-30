
package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import questforge.model.Question;

import java.util.List;

@Repository
public interface QuestionRepository
        extends JpaRepository<Question, Long> {

    List<Question> findByChallengeId(Long challengeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Question q WHERE q.challengeId = :challengeId")
    int deleteByChallengeId(@Param("challengeId") Long challengeId);
}
