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
    public UserStreak recordDailyActivity(Integer userId) {

        LocalDate today = LocalDate.now();

        UserStreak streak =
                userStreakRepository
                        .findByUserId(userId)
                        .orElseGet(() -> {

                            UserStreak newStreak =
                                    new UserStreak();

                            newStreak.setUserId(userId);
                            newStreak.setCurrentStreak(0);
                            newStreak.setLongestStreak(0);

                            return newStreak;
                        });

        LocalDate lastActiveDate =
                streak.getLastActiveDate();

        /*
         * User has already been active today.
         *
         * Do not increase the streak again.
         */
        if (today.equals(lastActiveDate)) {

            return streak;
        }

        /*
         * User was active yesterday.
         *
         * Continue the existing streak.
         */
        if (lastActiveDate != null
                && lastActiveDate
                        .plusDays(1)
                        .equals(today)) {

            int currentStreak =
                    streak.getCurrentStreak() == null
                            ? 0
                            : streak.getCurrentStreak();

            currentStreak++;

            streak.setCurrentStreak(
                    currentStreak
            );

        } else {

            /*
             * First activity OR previous streak was broken.
             */
            streak.setCurrentStreak(1);
        }

        int currentStreak =
                streak.getCurrentStreak() == null
                        ? 0
                        : streak.getCurrentStreak();

        int longestStreak =
                streak.getLongestStreak() == null
                        ? 0
                        : streak.getLongestStreak();

        /*
         * Update longest streak.
         */
        if (currentStreak > longestStreak) {

            streak.setLongestStreak(
                    currentStreak
            );
        }

        streak.setLastActiveDate(today);

        UserStreak savedStreak =
                userStreakRepository.save(streak);

        /*
         * Check streak achievements.
         */
        checkStreakAchievements(
                userId,
                currentStreak
        );

        return savedStreak;
    }

    private void checkStreakAchievements(
            Integer userId,
            int currentStreak) {

        /*
         * 3 DAY STREAK
         */
        if (currentStreak >= 3) {

            achievementService.unlockAchievement(
                    userId,
                    "STREAK_3",
                    "3 Day Streak",
                    "Maintain a 3-day activity streak.",
                    "🔥"
            );
        }

        /*
         * 7 DAY STREAK
         */
        if (currentStreak >= 7) {

            achievementService.unlockAchievement(
                    userId,
                    "STREAK_7",
                    "7 Day Streak",
                    "Maintain a 7-day activity streak.",
                    "🔥"
            );
        }

        /*
         * 30 DAY STREAK
         */
        if (currentStreak >= 30) {

            achievementService.unlockAchievement(
                    userId,
                    "STREAK_30",
                    "30 Day Streak",
                    "Maintain a 30-day activity streak.",
                    "💎"
            );
        }
    }

    public UserStreak getUserStreak(
            Integer userId) {

        return userStreakRepository
                .findByUserId(userId)
                .orElseGet(() -> {

                    UserStreak streak =
                            new UserStreak();

                    streak.setUserId(userId);
                    streak.setCurrentStreak(0);
                    streak.setLongestStreak(0);

                    return streak;
                });
    }
}