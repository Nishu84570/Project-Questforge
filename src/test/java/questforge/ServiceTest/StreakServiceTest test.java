
package questforge.ServiceTest;

import questforge.model.UserStreak;
import questforge.repository.UserStreakRepository;
import questforge.service.AchievementService;
import questforge.service.StreakService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreakServiceTest {

    @Mock
    private UserStreakRepository userStreakRepository;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private StreakService streakService;

    private final Long userId = 1L;

    private UserStreak streak;

    @BeforeEach
    void setUp() {
        streak = createStreak(
                userId,
                5L,
                10L,
                LocalDate.now().minusDays(1)
        );
    }


    private UserStreak createStreak(
            Long userId,
            Long currentStreak,
            Long longestStreak,
            LocalDate lastActiveDate
    ) {

        UserStreak userStreak = new UserStreak();

        userStreak.setUserId(userId);
        userStreak.setCurrentStreak(currentStreak);
        userStreak.setLongestStreak(longestStreak);
        userStreak.setLastActiveDate(lastActiveDate);

        return userStreak;
    }


    @Test
    void recordDailyActivity_NewUser() {

        when(userStreakRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        when(userStreakRepository.save(any(UserStreak.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserStreak result =
                streakService.recordDailyActivity(userId);

        assertNotNull(result);

        assertEquals(userId, result.getUserId());
        assertEquals(1L, result.getCurrentStreak());
        assertEquals(1L, result.getLongestStreak());
        assertEquals(LocalDate.now(), result.getLastActiveDate());

        verify(userStreakRepository).save(any(UserStreak.class));

        verifyNoInteractions(achievementService);
    }


    @Test
    void recordDailyActivity_ConsecutiveDay() {

        when(userStreakRepository.findByUserId(userId))
                .thenReturn(Optional.of(streak));

        when(userStreakRepository.save(any(UserStreak.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserStreak result =
                streakService.recordDailyActivity(userId);

        assertEquals(6L, result.getCurrentStreak());
        assertEquals(10L, result.getLongestStreak());
        assertEquals(LocalDate.now(), result.getLastActiveDate());

        verify(userStreakRepository).save(streak);
    }


    @Test
    void recordDailyActivity_SameDay() {

        streak.setLastActiveDate(LocalDate.now());

        when(userStreakRepository.findByUserId(userId))
                .thenReturn(Optional.of(streak));

        UserStreak result =
                streakService.recordDailyActivity(userId);

        assertNotNull(result);

        assertEquals(5L, result.getCurrentStreak());
        assertEquals(10L, result.getLongestStreak());

        verify(userStreakRepository, never())
                .save(any(UserStreak.class));

        verifyNoInteractions(achievementService);
    }



    @Test
    void recordDailyActivity_MissedDay() {

        streak.setLastActiveDate(LocalDate.now().minusDays(2));

        when(userStreakRepository.findByUserId(userId))
                .thenReturn(Optional.of(streak));

        when(userStreakRepository.save(any(UserStreak.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserStreak result =
                streakService.recordDailyActivity(userId);

        assertEquals(1L, result.getCurrentStreak());
        assertEquals(10L, result.getLongestStreak());

        assertEquals(LocalDate.now(), result.getLastActiveDate());

        verify(userStreakRepository).save(streak);
    }


    @Test
    void recordDailyActivity_UpdateLongestStreak() {

        streak.setCurrentStreak(10L);
        streak.setLongestStreak(10L);

        when(userStreakRepository.findByUserId(userId))
                .thenReturn(Optional.of(streak));

        when(userStreakRepository.save(any(UserStreak.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserStreak result =
                streakService.recordDailyActivity(userId);

        assertEquals(11L, result.getCurrentStreak());
        assertEquals(11L, result.getLongestStreak());
    }


    @Test
    void recordDailyActivity_ThreeDayAchievement() {

        streak.setCurrentStreak(2L);
        streak.setLongestStreak(2L);

        when(userStreakRepository.findByUserId(userId))
                .thenReturn(Optional.of(streak));

        when(userStreakRepository.save(any(UserStreak.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        streakService.recordDailyActivity(userId);

        assertEquals(3L, streak.getCurrentStreak());

        verify(achievementService).unlockAchievement(
                userId,
                "STREAK_3",
                "3 Day Streak",
                "Maintain a 3-day activity streak.",
                "🔥"
        );

        verify(achievementService, never()).unlockAchievement(
                eq(userId),
                eq("STREAK_7"),
                anyString(),
                anyString(),
                anyString()
        );
    }


    @Test
    void recordDailyActivity_SevenDayAchievement() {

        streak.setCurrentStreak(6L);
        streak.setLongestStreak(6L);

        when(userStreakRepository.findByUserId(userId))
                .thenReturn(Optional.of(streak));

        when(userStreakRepository.save(any(UserStreak.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        streakService.recordDailyActivity(userId);

        assertEquals(7L, streak.getCurrentStreak());

        verify(achievementService).unlockAchievement(
                userId,
                "STREAK_3",
                "3 Day Streak",
                "Maintain a 3-day activity streak.",
                "🔥"
        );

        verify(achievementService).unlockAchievement(
                userId,
                "STREAK_7",
                "7 Day Streak",
                "Maintain a 7-day activity streak.",
                "🔥"
        );

        verify(achievementService, never()).unlockAchievement(
                eq(userId),
                eq("STREAK_30"),
                anyString(),
                anyString(),
                anyString()
        );
    }


    @Test
    void recordDailyActivity_ThirtyDayAchievement() {

        streak.setCurrentStreak(29L);
        streak.setLongestStreak(29L);

        when(userStreakRepository.findByUserId(userId))
                .thenReturn(Optional.of(streak));

        when(userStreakRepository.save(any(UserStreak.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        streakService.recordDailyActivity(userId);

        assertEquals(30L, streak.getCurrentStreak());

        verify(achievementService).unlockAchievement(
                userId,
                "STREAK_3",
                "3 Day Streak",
                "Maintain a 3-day activity streak.",
                "🔥"
        );

        verify(achievementService).unlockAchievement(
                userId,
                "STREAK_7",
                "7 Day Streak",
                "Maintain a 7-day activity streak.",
                "🔥"
        );

        verify(achievementService).unlockAchievement(
                userId,
                "STREAK_30",
                "30 Day Streak",
                "Maintain a 30-day activity streak.",
                "💎"
        );
    }

    
    @Test
    void recordDailyActivity_InvalidUserId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> streakService.recordDailyActivity(0L)
        );

        verifyNoInteractions(
                userStreakRepository,
                achievementService
        );
    }



    @Test
    void recordDailyActivity_NullUserId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> streakService.recordDailyActivity(null)
        );

        verifyNoInteractions(
                userStreakRepository,
                achievementService
        );
    }


    @Test
    void getUserStreak_ExistingUser() {

        when(userStreakRepository.findByUserId(userId))
                .thenReturn(Optional.of(streak));

        UserStreak result =
                streakService.getUserStreak(userId);

        assertNotNull(result);

        assertEquals(userId, result.getUserId());
        assertEquals(5L, result.getCurrentStreak());
        assertEquals(10L, result.getLongestStreak());

        verify(userStreakRepository).findByUserId(userId);

        verifyNoInteractions(achievementService);
    }


    @Test
    void getUserStreak_NewUser() {

        when(userStreakRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        UserStreak result =
                streakService.getUserStreak(userId);

        assertNotNull(result);

        assertEquals(userId, result.getUserId());
        assertEquals(0L, result.getCurrentStreak());
        assertEquals(0L, result.getLongestStreak());

        verify(userStreakRepository).findByUserId(userId);

        verify(userStreakRepository, never())
                .save(any(UserStreak.class));

        verifyNoInteractions(achievementService);
    }



    @Test
    void getUserStreak_InvalidUserId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> streakService.getUserStreak(-1L)
        );

        verifyNoInteractions(
                userStreakRepository,
                achievementService
        );
    }



    @Test
    void getUserStreak_NullUserId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> streakService.getUserStreak(null)
        );

        verifyNoInteractions(
                userStreakRepository,
                achievementService
        );
    }
}
