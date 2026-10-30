
package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import questforge.model.Submission;

import java.util.List;

@Repository
public interface SubmissionRepository
        extends JpaRepository<Submission, Long> {

    List<Submission> findByUserId(Long userId);

    List<Submission> findByUserIdAndChallengeIdOrderBySubmittedAtAsc(
            Long userId,
            Long challengeId
    );


    List<Submission> findByFirstSubmissionTrue();


    List<Submission> findByChallengeIdAndFirstSubmissionTrue(Long challengeId);

    List<Submission> findByChallengeId(Long challengeId);
}
