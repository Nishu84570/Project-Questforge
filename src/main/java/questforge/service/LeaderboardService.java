
package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.GlobalLeaderboardEntryDTO;
import questforge.dto.LeaderboardEntryDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.model.User;
import questforge.repository.ChallengeRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    public LeaderboardService(
            SubmissionRepository submissionRepository,
            UserRepository userRepository,
            ChallengeRepository challengeRepository) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDTO> getChallengeLeaderboard(
            Long challengeId) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge not found with id: " + challengeId));

        List<Submission> submissions = new ArrayList<>(
                submissionRepository
                        .findByChallengeIdAndFirstSubmissionTrue(
                                challengeId));

        submissions.sort(
                Comparator.comparingLong(Submission::getScore)
                        .reversed()
                        .thenComparing(
                                Comparator.comparingLong(
                                        Submission::getCorrectAnswers)
                                        .reversed())
                        .thenComparing(
                                Submission::getSubmittedAt,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()))
                        .thenComparing(Submission::getId));

        List<Long> userIds = submissions.stream()
                .map(Submission::getUserId)
                .filter(userId -> userId != null)
                .distinct()
                .toList();

        Map<Long, User> users = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(
                        User::getId,
                        Function.identity()));

        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();

        int rank = 1;

        for (Submission submission : submissions) {
            User user = users.get(submission.getUserId());

            leaderboard.add(new LeaderboardEntryDTO(
                    rank++,
                    submission.getUserId(),
                    user != null ? user.getName() : "Unknown User",
                    challenge.getId(),
                    challenge.getTitle(),
                    submission.getScore(),
                    submission.getCorrectAnswers(),
                    submission.getTotalQuestions(),
                    submission.getSubmittedAt()));
        }

        return leaderboard;
    }

    @Transactional(readOnly = true)
    public List<GlobalLeaderboardEntryDTO> getGlobalLeaderboard() {

        List<User> users = new ArrayList<>(userRepository.findAll());

        users.sort(
                Comparator.comparingLong(this::getUserXp)
                        .reversed()
                        .thenComparing(User::getId));

        List<GlobalLeaderboardEntryDTO> leaderboard =
                new ArrayList<>();

        int rank = 1;

        for (User user : users) {
            long xp = getUserXp(user);
            int level = calculateLevel(xp);

            leaderboard.add(new GlobalLeaderboardEntryDTO(
                    rank++,
                    user.getId(),
                    user.getName(),
                    xp,
                    level));
        }

        return leaderboard;
    }

    private long getUserXp(User user) {
        return user.getXp();
    }

    private int calculateLevel(long xp) {

        if (xp < 100L) {
            return 1;
        }

        if (xp < 250L) {
            return 2;
        }

        if (xp < 450L) {
            return 3;
        }

        if (xp < 700L) {
            return 4;
        }

        if (xp < 1000L) {
            return 5;
        }

        return 6 + (int) ((xp - 1000L) / 500L);
    }
}
