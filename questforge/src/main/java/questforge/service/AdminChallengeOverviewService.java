package questforge.service;

import org.springframework.stereotype.Service;

import questforge.dto.AdminChallengeOverviewDTO;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.repository.ChallengeRepository;
import questforge.repository.SubmissionRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminChallengeOverviewService {

    private final ChallengeRepository challengeRepository;

    private final SubmissionRepository submissionRepository;

    public AdminChallengeOverviewService(
            ChallengeRepository challengeRepository,
            SubmissionRepository submissionRepository) {

        this.challengeRepository =
                challengeRepository;

        this.submissionRepository =
                submissionRepository;
    }

    public List<AdminChallengeOverviewDTO>
    getChallengeOverview() {

        List<Challenge> challenges =
                challengeRepository.findAll();

        List<Submission> submissions =
                submissionRepository.findAll();

        return challenges.stream()
                .map(challenge ->
                        buildChallengeOverview(
                                challenge,
                                submissions
                        )
                )
                .toList();
    }

    private AdminChallengeOverviewDTO
    buildChallengeOverview(
            Challenge challenge,
            List<Submission> allSubmissions) {

        List<Submission> challengeSubmissions =
                allSubmissions.stream()
                        .filter(submission ->
                                challenge.getId().equals(
                                        submission.getChallengeId()
                                )
                        )
                        .toList();

        long totalSubmissions =
                challengeSubmissions.size();

        long firstSubmissions =
                challengeSubmissions.stream()
                        .filter(submission ->
                                Boolean.TRUE.equals(
                                        submission.getFirstSubmission()
                                )
                        )
                        .count();

        Set<Integer> uniqueUserIds =
                challengeSubmissions.stream()
                        .map(Submission::getUserId)
                        .filter(userId ->
                                userId != null
                        )
                        .collect(
                                Collectors.toCollection(
                                        HashSet::new
                                )
                        );

        long uniqueParticipants =
                uniqueUserIds.size();

        List<Integer> scoredValues =
                challengeSubmissions.stream()
                        .map(Submission::getScore)
                        .filter(score ->
                                score != null
                        )
                        .toList();

        double averageScore =
                scoredValues.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0);

        Integer highestScore =
                scoredValues.stream()
                        .max(Integer::compareTo)
                        .orElse(null);

        Integer lowestScore =
                scoredValues.stream()
                        .min(Integer::compareTo)
                        .orElse(null);


        double completionRate = 0.0;

        if (firstSubmissions > 0) {

            long completedFirstSubmissions =
                    challengeSubmissions.stream()
                            .filter(submission ->
                                    Boolean.TRUE.equals(
                                            submission.getFirstSubmission()
                                    )
                            )
                            .filter(submission ->
                                    submission.getScore() != null
                            )
                            .count();

            completionRate =
                    (completedFirstSubmissions * 100.0)
                            / firstSubmissions;
        }

        return new AdminChallengeOverviewDTO(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDifficulty(),
                challenge.getCategory(),
                totalSubmissions,
                firstSubmissions,
                uniqueParticipants,
                averageScore,
                highestScore,
                lowestScore,
                completionRate
        );
    }
}