
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.Submission;
import questforge.repository.SubmissionRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SubmissionRepositoryTest {

    @Autowired
    private SubmissionRepository submissionRepository;

    private Submission createSubmission(
            Long userId,
            Long challengeId,
            LocalDateTime submittedAt,
            Boolean firstSubmission
    ) {
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setChallengeId(challengeId);
        submission.setSubmittedAt(submittedAt);
        submission.setFirstSubmission(firstSubmission);
        submission.setAnswer("Test answer");
        submission.setStatus("COMPLETED");
        submission.setTotalQuestions(10L);
        submission.setCorrectAnswers(8L);
        submission.setScore(80L);

        return submission;
    }

    @Test
    void shouldSaveAndFindSubmissionById() {
        Submission saved = submissionRepository.save(
                createSubmission(1L, 10L, LocalDateTime.now(), true)
        );

        Submission found = submissionRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getUserId()).isEqualTo(1L);
        assertThat(found.getChallengeId()).isEqualTo(10L);
        assertThat(found.getScore()).isEqualTo(80L);
    }

    @Test
    void shouldFindSubmissionsByUserId() {
        submissionRepository.save(
                createSubmission(1L, 10L, LocalDateTime.now(), true)
        );
        submissionRepository.save(
                createSubmission(1L, 20L, LocalDateTime.now(), false)
        );
        submissionRepository.save(
                createSubmission(2L, 10L, LocalDateTime.now(), true)
        );

        List<Submission> result = submissionRepository.findByUserId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(s -> s.getUserId().equals(1L));
    }

    @Test
    void shouldReturnEmptyListForUnknownUser() {
        assertThat(submissionRepository.findByUserId(99L)).isEmpty();
    }

    @Test
    void shouldFindSubmissionsByUserAndChallengeInAscendingOrder() {
        LocalDateTime now = LocalDateTime.now();

        submissionRepository.save(
                createSubmission(1L, 10L, now.plusHours(2), false)
        );
        submissionRepository.save(
                createSubmission(1L, 10L, now, true)
        );
        submissionRepository.save(
                createSubmission(2L, 10L, now, true)
        );

        List<Submission> result =
                submissionRepository.findByUserIdAndChallengeIdOrderBySubmittedAtAsc(
                        1L, 10L
                );

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSubmittedAt())
                .isBefore(result.get(1).getSubmittedAt());
    }

    @Test
    void shouldFindAllFirstSubmissions() {
        submissionRepository.save(
                createSubmission(1L, 10L, LocalDateTime.now(), true)
        );
        submissionRepository.save(
                createSubmission(2L, 10L, LocalDateTime.now(), true)
        );
        submissionRepository.save(
                createSubmission(3L, 10L, LocalDateTime.now(), false)
        );

        List<Submission> result =
                submissionRepository.findByFirstSubmissionTrue();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(s -> Boolean.TRUE.equals(s.getFirstSubmission()));
    }

    @Test
    void shouldFindFirstSubmissionsByChallengeId() {
        submissionRepository.save(
                createSubmission(1L, 10L, LocalDateTime.now(), true)
        );
        submissionRepository.save(
                createSubmission(2L, 10L, LocalDateTime.now(), false)
        );
        submissionRepository.save(
                createSubmission(3L, 20L, LocalDateTime.now(), true)
        );

        List<Submission> result =
                submissionRepository.findByChallengeIdAndFirstSubmissionTrue(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChallengeId()).isEqualTo(10L);
        assertThat(result.get(0).getFirstSubmission()).isTrue();
    }

    @Test
    void shouldFindSubmissionsByChallengeId() {
        submissionRepository.save(
                createSubmission(1L, 10L, LocalDateTime.now(), true)
        );
        submissionRepository.save(
                createSubmission(2L, 10L, LocalDateTime.now(), false)
        );
        submissionRepository.save(
                createSubmission(3L, 20L, LocalDateTime.now(), true)
        );

        List<Submission> result = submissionRepository.findByChallengeId(10L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(s -> s.getChallengeId().equals(10L));
    }
}
