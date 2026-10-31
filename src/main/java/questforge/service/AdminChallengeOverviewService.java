
package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminChallengeOverviewDTO> getChallengeOverview() {

        List<Challenge> challenges = challengeRepository.findAll();
        List<Submission> submissions = submissionRepository.findAll();

        return challenges.stream()
                .map(challenge ->
                        buildChallengeOverview(challenge, submissions))
                .toList();
    }

    private AdminChallengeOverviewDTO buildChallengeOverview(
            Challenge challenge,
            List<Submission> allSubmissions) {

        List<Submission> challengeSubmissions = allSubmissions.stream()
                .filter(submission ->
                        challenge.getId().equals(
                                submission.getChallengeId()))
                .toList();

        long totalSubmissions = challengeSubmissions.size();

        List<Submission> firstSubmissions = challengeSubmissions.stream()
                .filter(submission ->
                        Boolean.TRUE.equals(
                                submission.getFirstSubmission()))
                .toList();

        long firstSubmissionCount = firstSubmissions.size();

        Set<Long> uniqueUserIds = challengeSubmissions.stream()
                .map(Submission::getUserId)
                .filter(userId -> userId != null)
                .collect(Collectors.toCollection(HashSet::new));

        long uniqueParticipants = uniqueUserIds.size();

        double averageScore = challengeSubmissions.stream()
                .mapToLong(Submission::getScore)
                .average()
                .orElse(0.0);

        Long highestScore = challengeSubmissions.stream()
                .map(Submission::getScore)
                .max(Long::compareTo)
                .orElse(null);

        Long lowestScore = challengeSubmissions.stream()
                .map(Submission::getScore)
                .min(Long::compareTo)
                .orElse(null);

        Set<Long> usersWithPerfectFirstAttempt = new HashSet<>();

        for (Submission submission : firstSubmissions) {
            if (submission.getUserId() != null
                    && submission.getScore() >= 100L) {

                usersWithPerfectFirstAttempt.add(
                        submission.getUserId());
            }
        }

        double completionRate = uniqueParticipants == 0L
                ? 0.0
                : usersWithPerfectFirstAttempt.size()
                        * 100.0 / uniqueParticipants;

        return new AdminChallengeOverviewDTO(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDifficulty(),
                challenge.getCategory(),
                totalSubmissions,
                firstSubmissionCount,
                uniqueParticipants,
                averageScore,
                highestScore,
                lowestScore,
                completionRate);
    }
}
