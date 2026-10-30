
package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.AdminDashboardResponseDTO;
import questforge.dto.AdminDashboardResponseDTO.MostActiveChallengeDTO;
import questforge.dto.AdminDashboardResponseDTO.TopUserDTO;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.model.User;
import questforge.repository.AchievementRepository;
import questforge.repository.ChallengeRepository;
import questforge.repository.NotificationRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final AchievementRepository achievementRepository;
    private final NotificationRepository notificationRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            ChallengeRepository challengeRepository,
            SubmissionRepository submissionRepository,
            AchievementRepository achievementRepository,
            NotificationRepository notificationRepository) {

        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.achievementRepository = achievementRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponseDTO getDashboardStats() {

        List<User> users = userRepository.findAll();
        List<Submission> submissions = submissionRepository.findAll();
        List<Challenge> challenges = challengeRepository.findAll();

        long totalUsers = users.size();
        long totalChallenges = challenges.size();
        long totalSubmissions = submissions.size();

        long totalAchievements = achievementRepository.count();
        long totalNotifications = notificationRepository.count();

        // Total XP across users
        long totalXp = users.stream()
                .mapToLong(this::getUserXp)
                .sum();

        // Average user XP
        double averageUserXp = users.stream()
                .mapToLong(this::getUserXp)
                .average()
                .orElse(0.0);

        // Average submission score
        double averageSubmissionScore = submissions.stream()
                .mapToLong(Submission::getScore)
                .average()
                .orElse(0.0);

        // Top user
        TopUserDTO topUser = users.stream()
                .max(
                        Comparator.comparingLong(this::getUserXp)
                                .thenComparing(
                                        User::getId,
                                        Comparator.reverseOrder()
                                )
                )
                .map(user ->
                        new TopUserDTO(
                                user.getId(),
                                user.getName(),
                                getUserXp(user)
                        )
                )
                .orElse(null);

        // Most active challenge by submission count
        Map<Long, Long> submissionCounts = new HashMap<>();

        for (Submission submission : submissions) {

            Long challengeId = submission.getChallengeId();

            if (challengeId == null) {
                continue;
            }

            submissionCounts.merge(
                    challengeId,
                    1L,
                    Long::sum
            );
        }

        MostActiveChallengeDTO mostActiveChallenge = null;

        if (!submissionCounts.isEmpty()) {

            Map.Entry<Long, Long> mostActiveEntry =
                    submissionCounts.entrySet()
                            .stream()
                            .max(
                                    Map.Entry.<Long, Long>comparingByValue()
                                            .thenComparing(
                                                    Map.Entry::getKey,
                                                    Comparator.reverseOrder()
                                            )
                            )
                            .orElse(null);

            if (mostActiveEntry != null) {

                Long challengeId = mostActiveEntry.getKey();
                long submissionCount = mostActiveEntry.getValue();

                Challenge challenge = challenges.stream()
                        .filter(item ->
                                challengeId.equals(item.getId()))
                        .findFirst()
                        .orElse(null);

                String challengeTitle = challenge != null
                        ? challenge.getTitle()
                        : "Deleted Challenge";

                mostActiveChallenge = new MostActiveChallengeDTO(
                        challengeId,
                        challengeTitle,
                        submissionCount
                );
            }
        }

        return new AdminDashboardResponseDTO(
                totalUsers,
                totalChallenges,
                totalSubmissions,
                totalAchievements,
                totalNotifications,
                totalXp,
                averageUserXp,
                averageSubmissionScore,
                topUser,
                mostActiveChallenge
        );
    }

    private long getUserXp(User user) {
        return user.getXp();
    }
}
