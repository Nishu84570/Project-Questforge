package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.model.User;
import questforge.repository.UserRepository;

@Service
public class XPService {

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    public XPService(
            UserRepository userRepository,
            NotificationService notificationService) {

        this.userRepository =
                userRepository;

        this.notificationService =
                notificationService;
    }

    // ==========================================
    // ADD XP
    // ==========================================

    @Transactional
    public User addXp(
            Integer userId,
            int xpAmount) {

        if (xpAmount <= 0) {
            throw new IllegalArgumentException(
                    "XP amount must be greater than 0"
            );
        }

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "User not found"
                                )
                        );

        int currentXp =
                user.getXp() == null
                        ? 0
                        : user.getXp();

        /*
         * Calculate level before XP is added.
         */
        int oldLevel =
                calculateLevel(currentXp);

        /*
         * Add XP.
         */
        int newXp =
                currentXp + xpAmount;

        user.setXp(
                newXp
        );

        /*
         * Calculate level after XP is added.
         */
        int newLevel =
                calculateLevel(newXp);

        User savedUser =
                userRepository.save(user);

        /*
         * LEVEL UP NOTIFICATION
         *
         * Only create notification when
         * the user's level actually increases.
         */
        if (newLevel > oldLevel) {

            notificationService.createNotification(
                    userId,
                    "Level Up!",
                    "Congratulations! You reached Level "
                            + newLevel
                            + ".",
                    "LEVEL_UP"
            );
        }

        return savedUser;
    }

    // ==========================================
    // CHALLENGE XP
    // ==========================================

    @Transactional
    public User awardChallengeXp(
            Integer userId,
            String difficulty) {

        int xpReward =
                calculateChallengeXp(
                        difficulty
                );

        return addXp(
                userId,
                xpReward
        );
    }

    // ==========================================
    // CALCULATE CHALLENGE XP
    // ==========================================

    public int calculateChallengeXp(
            String difficulty) {

        if (difficulty == null) {
            throw new IllegalArgumentException(
                    "Difficulty is required"
            );
        }

        return switch (
                difficulty.trim().toUpperCase()
        ) {

            case "EASY" -> 50;

            case "MEDIUM" -> 100;

            case "HARD" -> 150;

            default -> throw new IllegalArgumentException(
                    "Invalid challenge difficulty: "
                            + difficulty
            );
        };
    }

    // ==========================================
    // DAILY QUEST XP
    // ==========================================

    @Transactional
    public User awardDailyQuestXp(
            Integer userId,
            int rewardXp) {

        return addXp(
                userId,
                rewardXp
        );
    }

    // ==========================================
    // CALCULATE LEVEL
    // ==========================================

    public int calculateLevel(
            int xp) {

        if (xp < 100) {
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
}