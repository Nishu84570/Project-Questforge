package questforge.service;

import org.springframework.stereotype.Service;

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

        this.userRepository =
                userRepository;

        this.challengeRepository =
                challengeRepository;

        this.submissionRepository =
                submissionRepository;

        this.achievementRepository =
                achievementRepository;

        this.notificationRepository =
                notificationRepository;
    }

    public AdminDashboardResponseDTO getDashboardStats() {

        List<User> users =
                userRepository.findAll();

        List<Submission> submissions =
                submissionRepository.findAll();

        List<Challenge> challenges =
                challengeRepository.findAll();

        long totalUsers =
                users.size();

        long totalChallenges =
                challenges.size();

        long totalSubmissions =
                submissions.size();

        long totalAchievements =
                achievementRepository.count();

        long totalNotifications =
                notificationRepository.count();

        // ==========================================
        // TOTAL XP
        // ==========================================

        long totalXp =
                users.stream()
                        .mapToLong(user ->
                                user.getXp() == null
                                        ? 0
                                        : user.getXp()
                        )
                        .sum();

        // ==========================================
        // AVERAGE USER XP
        // ==========================================

        double averageUserXp =
                users.stream()
                        .mapToInt(user ->
                                user.getXp() == null
                                        ? 0
                                        : user.getXp()
                        )
                        .average()
                        .orElse(0.0);

        // ==========================================
        // AVERAGE SUBMISSION SCORE
        // ==========================================

        double averageSubmissionScore =
                submissions.stream()
                        .filter(submission ->
                                submission.getScore() != null
                        )
                        .mapToInt(
                                Submission::getScore
                        )
                        .average()
                        .orElse(0.0);

        // ==========================================
        // TOP USER
        // ==========================================

        TopUserDTO topUser =
                users.stream()
                        .max(
                                Comparator
                                        .comparing(
                                                this::getUserXp
                                        )
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

        // ==========================================
        // MOST ACTIVE CHALLENGE
        // ==========================================

        Map<Integer, Long> submissionCounts =
                new HashMap<>();

        for (Submission submission :
                submissions) {

            Integer challengeId =
                    submission.getChallengeId();

            if (challengeId == null) {
                continue;
            }

            submissionCounts.merge(
                    challengeId,
                    1L,
                    Long::sum
            );
        }

        MostActiveChallengeDTO
                mostActiveChallenge = null;

        if (!submissionCounts.isEmpty()) {

            Map.Entry<Integer, Long>
                    mostActiveEntry =
                    submissionCounts
                            .entrySet()
                            .stream()
                            .max(
                                    Map.Entry.comparingByValue()
                            )
                            .orElse(null);

            if (mostActiveEntry != null) {

                Integer challengeId =
                        mostActiveEntry.getKey();

                long submissionCount =
                        mostActiveEntry.getValue();

                Challenge challenge =
                        challenges.stream()
                                .filter(item ->
                                        challengeId.equals(
                                                item.getId()
                                        )
                                )
                                .findFirst()
                                .orElse(null);

                String challengeTitle;

                if (challenge != null) {

                    challengeTitle =
                            challenge.getTitle();

                } else {

                    challengeTitle =
                            "Deleted Challenge";
                }

                mostActiveChallenge =
                        new MostActiveChallengeDTO(
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

    private Integer getUserXp(
            User user) {

        if (user.getXp() == null) {
            return 0;
        }

        return user.getXp();
    }
}