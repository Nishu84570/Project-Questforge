package questforge.service;

import org.springframework.stereotype.Service;

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

        this.challengeRepository =
                challengeRepository;

        this.submissionRepository =
                submissionRepository;
    }

    public ChallengeAnalyticsResponseDTO
    getChallengeAnalytics(
            Integer challengeId) {

        Challenge challenge =
                challengeRepository
                        .findById(challengeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Challenge not found with id: "
                                                + challengeId
                                )
                        );

        List<Submission> submissions =
                submissionRepository
                        .findAll()
                        .stream()
                        .filter(submission ->
                                challengeId.equals(
                                        submission.getChallengeId()
                                )
                        )
                        .toList();

        long totalSubmissions =
                submissions.size();

        List<Submission> firstSubmissions =
                submissions
                        .stream()
                        .filter(submission ->
                                Boolean.TRUE.equals(
                                        submission
                                                .getFirstSubmission()
                                )
                        )
                        .toList();

        long firstSubmissionCount =
                firstSubmissions.size();

        double averageScore =
                firstSubmissions
                        .stream()
                        .filter(submission ->
                                submission.getScore() != null
                        )
                        .mapToInt(
                                Submission::getScore
                        )
                        .average()
                        .orElse(0.0);

        long completedSubmissions =
                firstSubmissions
                        .stream()
                        .filter(submission ->
                                submission.getScore() != null
                        )
                        .count();

        double completionRate = 0.0;

        if (firstSubmissionCount > 0) {

            completionRate =
                    (completedSubmissions * 100.0)
                            / firstSubmissionCount;
        }

        return new ChallengeAnalyticsResponseDTO(
                challenge.getId(),
                challenge.getTitle(),
                totalSubmissions,
                firstSubmissionCount,
                averageScore,
                completionRate
        );
    }
}