package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import questforge.model.Submission;

import java.util.List;

public interface SubmissionRepository
        extends JpaRepository<Submission, Integer> {

    // Get all submissions of a user
    List<Submission> findByUserId(Integer userId);

    // Get all attempts of a user for a specific challenge
    // Oldest submission first
    List<Submission> findByUserIdAndChallengeIdOrderBySubmittedAtAsc(
            Integer userId,
            Integer challengeId
    );

    // Get all first submissions
    // Useful for leaderboard
    List<Submission> findByFirstSubmissionTrue();

    // Get first submissions for a particular challenge
    // Useful for challenge leaderboard
    List<Submission> findByChallengeIdAndFirstSubmissionTrue(
            Integer challengeId
    );
}