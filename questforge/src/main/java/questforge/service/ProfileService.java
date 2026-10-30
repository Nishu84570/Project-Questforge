package questforge.service;

import org.springframework.stereotype.Service;

import questforge.dto.AchievementResponseDTO;
import questforge.dto.ProfileProgressDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Submission;
import questforge.model.User;
import questforge.model.UserStreak;
import questforge.repository.AchievementRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final AchievementRepository achievementRepository;
    private final StreakService streakService;

    public ProfileService(
            UserRepository userRepository,
            SubmissionRepository submissionRepository,
            AchievementRepository achievementRepository,
            StreakService streakService) {

        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.achievementRepository = achievementRepository;
        this.streakService = streakService;
    }

    public ProfileProgressDTO getMyProfileProgress(
            String email) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Integer userId =
                user.getId();

        List<Submission> firstSubmissions =
                submissionRepository
                        .findByUserId(userId)
                        .stream()
                        .filter(
                                submission ->
                                        Boolean.TRUE.equals(
                                                submission
                                                        .getFirstSubmission()
                                        )
                        )
                        .toList();

        Set<Integer> attemptedChallengeIds =
                new HashSet<>();

        for (Submission submission :
                firstSubmissions) {

            attemptedChallengeIds.add(
                    submission.getChallengeId()
            );
        }

        int challengesAttempted =
                attemptedChallengeIds.size();

        int challengesCompleted =
                (int) firstSubmissions
                        .stream()
                        .filter(
                                submission ->
                                        submission.getScore() != null
                                                && submission.getScore() == 100
                        )
                        .count();

        int averageScore = 0;

        if (!firstSubmissions.isEmpty()) {

            int totalScore =
                    firstSubmissions
                            .stream()
                            .mapToInt(
                                    submission ->
                                            submission.getScore() == null
                                                    ? 0
                                                    : submission.getScore()
                            )
                            .sum();

            averageScore =
                    totalScore
                            / firstSubmissions.size();
        }

        int xp =
                user.getXp() == null
                        ? 0
                        : user.getXp();

        int level =
                calculateLevel(xp);

        int nextLevelXp =
                calculateNextLevelXp(level);

        int xpToNextLevel =
                Math.max(
                        0,
                        nextLevelXp - xp
                );

        List<User> users =
                userRepository.findAll();

        users.sort(
                Comparator
                        .comparing(
                                (User item) ->
                                        item.getXp() == null
                                                ? 0
                                                : item.getXp(),
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                User::getId
                        )
        );

        int globalRank = 1;

        for (int i = 0;
             i < users.size();
             i++) {

            if (users.get(i)
                    .getId()
                    .equals(userId)) {

                globalRank = i + 1;
                break;
            }
        }

        List<AchievementResponseDTO> achievements =
                achievementRepository
                        .findByUserId(userId)
                        .stream()
                        .map(
                                achievement ->
                                        new AchievementResponseDTO(
                                                achievement.getId(),
                                                achievement.getCode(),
                                                achievement.getName(),
                                                achievement.getDescription(),
                                                achievement.getIcon()
                                        )
                        )
                        .toList();

        UserStreak streak =
                streakService.getUserStreak(userId);

        int currentStreak =
                streak.getCurrentStreak() == null
                        ? 0
                        : streak.getCurrentStreak();

        int longestStreak =
                streak.getLongestStreak() == null
                        ? 0
                        : streak.getLongestStreak();

        return new ProfileProgressDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                xp,
                level,
                nextLevelXp,
                xpToNextLevel,
                challengesAttempted,
                challengesCompleted,
                averageScore,
                globalRank,
                achievements,
                currentStreak,
                longestStreak
        );
    }

    private int calculateLevel(Integer xp) {

        if (xp == null || xp < 100) {
            return 1;
        }

        if (xp < 250) {
            return 2;
        }

        if (xp < 450) {
            return 3;
        }

        if (xp < 700) {
            return 4;
        }

        if (xp < 1000) {
            return 5;
        }

        return 6 + ((xp - 1000) / 500);
    }

    private int calculateNextLevelXp(
            int level) {

        return switch (level) {

            case 1 -> 100;
            case 2 -> 250;
            case 3 -> 450;
            case 4 -> 700;
            case 5 -> 1000;

            default ->
                    1000 + ((level - 5) * 500);
        };
    }
}