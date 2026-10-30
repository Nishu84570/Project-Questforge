package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public ProfileProgressDTO getMyProfileProgress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Long userId = user.getId();

        List<Submission> firstSubmissions =
                submissionRepository.findByUserId(userId)
                        .stream()
                        .filter(submission ->
                                Boolean.TRUE.equals(
                                        submission.getFirstSubmission()
                                )
                        )
                        .toList();

        Set<Long> attemptedChallengeIds = new HashSet<>();

        for (Submission submission : firstSubmissions) {
            attemptedChallengeIds.add(submission.getChallengeId());
        }

        long challengesAttempted = attemptedChallengeIds.size();

        long challengesCompleted = firstSubmissions.stream()
                .filter(submission -> submission.getScore() >= 100L)
                .count();

        long totalScore = firstSubmissions.stream()
                .mapToLong(Submission::getScore)
                .sum();

        int averageScore = firstSubmissions.isEmpty()
                ? 0
                : (int) Math.round(
                        (double) totalScore / firstSubmissions.size()
                );

        long xp = user.getXp();
        int level = calculateLevel(xp);
        long nextLevelXp = calculateNextLevelXp(level);
        long xpToNextLevel = Math.max(0L, nextLevelXp - xp);

        List<User> users = userRepository.findAll();

        users.sort(
                Comparator.comparingLong(User::getXp)
                        .reversed()
                        .thenComparing(User::getId)
        );

        int globalRank = 1;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(userId)) {
                globalRank = i + 1;
                break;
            }
        }

        List<AchievementResponseDTO> achievements =
                achievementRepository.findByUserId(userId)
                        .stream()
                        .map(achievement ->
                                new AchievementResponseDTO(
                                        achievement.getId(),
                                        achievement.getCode(),
                                        achievement.getName(),
                                        achievement.getDescription(),
                                        achievement.getIcon()
                                )
                        )
                        .toList();

        UserStreak streak = streakService.getUserStreak(userId);

        long currentStreak = streak.getCurrentStreak();
        long longestStreak = streak.getLongestStreak();

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

    private long calculateNextLevelXp(int level) {
        return switch (level) {
            case 1 -> 100L;
            case 2 -> 250L;
            case 3 -> 450L;
            case 4 -> 700L;
            case 5 -> 1000L;
            default -> 1000L + ((long) (level - 5) * 500L);
        };
    }
}