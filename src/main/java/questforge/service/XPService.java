package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.exception.ResourceNotFoundException;
import questforge.model.User;
import questforge.repository.UserRepository;

@Service
public class XPService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public XPService(
            UserRepository userRepository,
            NotificationService notificationService) {

        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }



    @Transactional
    public User addXp(Long userId, long xpAmount) {
        if (userId == null || userId <= 0L) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (xpAmount <= 0L) {
            throw new IllegalArgumentException(
                    "XP amount must be greater than 0"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        long currentXp = user.getXp();

        if (Long.MAX_VALUE - currentXp < xpAmount) {
            throw new IllegalArgumentException(
                    "XP total exceeds the allowed range"
            );
        }

        int oldLevel = calculateLevel(currentXp);
        long newXp = currentXp + xpAmount;
        int newLevel = calculateLevel(newXp);

        user.setXp(newXp);
        User savedUser = userRepository.save(user);

        if (newLevel > oldLevel) {
            notificationService.createNotification(
                    userId,
                    "Level Up!",
                    "Congratulations! You reached Level "
                            + newLevel + ".",
                    "LEVEL_UP"
            );
        }

        return savedUser;
    }

    @Transactional
    public User awardChallengeXp(Long userId, String difficulty) {
        long xpReward = calculateChallengeXp(difficulty);
        return addXp(userId, xpReward);
    }


    public long calculateChallengeXp(String difficulty) {
        if (difficulty == null || difficulty.isBlank()) {
            throw new IllegalArgumentException(
                    "Difficulty is required"
            );
        }

        return switch (difficulty.trim().toUpperCase()) {
            case "EASY" -> 50L;
            case "MEDIUM" -> 100L;
            case "HARD" -> 150L;
            default -> throw new IllegalArgumentException(
                    "Invalid challenge difficulty: " + difficulty
            );
        };
    }


    @Transactional
    public User awardDailyQuestXp(Long userId, long rewardXp) {
        return addXp(userId, rewardXp);
    }


    public int calculateLevel(long xp) {
        if (xp < 100L) return 1;
        if (xp < 250L) return 2;
        if (xp < 450L) return 3;
        if (xp < 700L) return 4;
        if (xp < 1000L) return 5;

        long level = 6L + ((xp - 1000L) / 500L);
        return (int) Math.min(level, Integer.MAX_VALUE);
    }
}