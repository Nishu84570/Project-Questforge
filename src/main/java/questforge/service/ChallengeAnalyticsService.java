
package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.ChallengeAnalyticsResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.repository.ChallengeRepository;
import questforge.repository.SubmissionRepository;

import java.util.List;

@Service
public class ChallengeAnalyticsService {

    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;

    public ChallengeAnalyticsService(
            ChallengeRepository challengeRepository,
            SubmissionRepository submissionRepository) {
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional(readOnly = true)
    public ChallengeAnalyticsResponseDTO getChallengeAnalytics(
            Long challengeId) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge not found with id: " + challengeId));

        List<Submission> submissions =
                submissionRepository.findByChallengeId(challengeId);

        long totalSubmissions = submissions.size();

        List<Submission> firstSubmissions = submissions.stream()
                .filter(submission ->
                        Boolean.TRUE.equals(
                                submission.getFirstSubmission()))
                .toList();

        long firstSubmissionCount = firstSubmissions.size();

        double averageScore = firstSubmissions.stream()
                .mapToLong(Submission::getScore)
                .average()
                .orElse(0.0);

        long completedSubmissions = firstSubmissions.stream()
                .filter(submission ->
                        "COMPLETED".equalsIgnoreCase(
                                submission.getStatus())
                        || "APPROVED".equalsIgnoreCase(
                                submission.getStatus()))
                .count();

        double completionRate = firstSubmissionCount == 0
                ? 0.0
                : completedSubmissions * 100.0 / firstSubmissionCount;

        return new ChallengeAnalyticsResponseDTO(
                challenge.getId(),
                challenge.getTitle(),
                totalSubmissions,
                firstSubmissionCount,
                averageScore,
                completionRate);
    }
}
