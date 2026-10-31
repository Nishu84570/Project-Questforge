package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.model.UserStreak;
import questforge.repository.UserStreakRepository;

import java.time.LocalDate;

@Service
public class StreakService {

    private final UserStreakRepository userStreakRepository;
    private final AchievementService achievementService;

    public StreakService(
            UserStreakRepository userStreakRepository,
            AchievementService achievementService) {

        this.userStreakRepository = userStreakRepository;
        this.achievementService = achievementService;
    }

    @Transactional
    public UserStreak recordDailyActivity(Long userId) {
        if (userId == null || userId <= 0L) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        LocalDate today = LocalDate.now();

        UserStreak streak = userStreakRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserStreak newStreak = new UserStreak();
                    newStreak.setUserId(userId);
                    newStreak.setCurrentStreak(0L);
                    newStreak.setLongestStreak(0L);
                    return newStreak;
                });

        LocalDate lastActiveDate = streak.getLastActiveDate();

        if (today.equals(lastActiveDate)) {
            return streak;
        }

        long currentStreak;

        if (lastActiveDate != null
                && lastActiveDate.plusDays(1).equals(today)) {
            currentStreak = streak.getCurrentStreak() + 1L;
        } 
        
        else {
         
            currentStreak = 1L;
        }

        long longestStreak = Math.max(
                streak.getLongestStreak(),
                currentStreak
        );

        streak.setCurrentStreak(currentStreak);
        streak.setLongestStreak(longestStreak);
        streak.setLastActiveDate(today);

        UserStreak savedStreak = userStreakRepository.save(streak);

        checkStreakAchievements(userId, currentStreak);

        return savedStreak;
    }

    private void checkStreakAchievements(
            Long userId,
            long currentStreak) {

        if (currentStreak >= 3L) {
            achievementService.unlockAchievement(
                    userId,
                    "STREAK_3",
                    "3 Day Streak",
                    "Maintain a 3-day activity streak.",
                    "🔥"
            );
        }

        if (currentStreak >= 7L) {
            achievementService.unlockAchievement(
                    userId,
                    "STREAK_7",
                    "7 Day Streak",
                    "Maintain a 7-day activity streak.",
                    "🔥"
            );
        }

        if (currentStreak >= 30L) {
            achievementService.unlockAchievement(
                    userId,
                    "STREAK_30",
                    "30 Day Streak",
                    "Maintain a 30-day activity streak.",
                    "💎"
            );
        }
    }

    @Transactional(readOnly = true)
    public UserStreak getUserStreak(Long userId) {
        if (userId == null || userId <= 0L) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userStreakRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserStreak streak = new UserStreak();
                    streak.setUserId(userId);
                    streak.setCurrentStreak(0L);
                    streak.setLongestStreak(0L);
                    return streak;
                });
    }
}