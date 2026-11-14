
package questforge.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import questforge.exception.ResourceNotFoundException;
import questforge.model.User;
import questforge.repository.UserRepository;
import questforge.service.NotificationService;
import questforge.service.XPService;

@ExtendWith(MockitoExtension.class)
class XPServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private XPService xpService;

    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
    }


    private User prepareUserWithXp(long currentXp) {
        when(user.getXp()).thenReturn(currentXp);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        return user;
    }



    @Test
    void addXp_shouldAddXpSuccessfully() {
        prepareUserWithXp(100L);

        User result = xpService.addXp(1L, 50L);

        assertSame(user, result);
        verify(user).setXp(150L);
        verify(userRepository).save(user);
    }

    @Test
    void addXp_shouldReturnSavedUser() {
        prepareUserWithXp(200L);

        User result = xpService.addXp(1L, 10L);

        assertNotNull(result);
        assertSame(user, result);
    }

    @Test
    void addXp_shouldNotSendNotificationWhenLevelDoesNotIncrease() {
        prepareUserWithXp(100L);

        xpService.addXp(1L, 50L);

        verify(notificationService, never())
                .createNotification(
                        anyLong(),
                        anyString(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void addXp_shouldSendNotificationWhenLevelIncreases() {
        prepareUserWithXp(99L);

        xpService.addXp(1L, 1L);

        verify(notificationService).createNotification(
                1L,
                "Level Up!",
                "Congratulations! You reached Level 2.",
                "LEVEL_UP"
        );
    }

    @Test
    void addXp_shouldNotifyOnlyWhenNewLevelIsGreater() {
        prepareUserWithXp(249L);

        xpService.addXp(1L, 1L);

        verify(notificationService).createNotification(
                1L,
                "Level Up!",
                "Congratulations! You reached Level 3.",
                "LEVEL_UP"
        );
    }


    @Test
    void addXp_shouldThrowExceptionWhenUserIdIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.addXp(null, 50L)
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(notificationService);
    }

    @Test
    void addXp_shouldThrowExceptionWhenUserIdIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.addXp(0L, 50L)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void addXp_shouldThrowExceptionWhenUserIdIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.addXp(-1L, 50L)
        );

        verifyNoInteractions(userRepository);
    }


    @Test
    void addXp_shouldThrowExceptionWhenXpIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.addXp(1L, 0L)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void addXp_shouldThrowExceptionWhenXpIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.addXp(1L, -50L)
        );

        verifyNoInteractions(userRepository);
    }



    @Test
    void addXp_shouldThrowResourceNotFoundWhenUserDoesNotExist() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> xpService.addXp(1L, 50L)
        );

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(notificationService);
    }



   
@Test
void addXp_shouldThrowExceptionWhenXpExceedsLongMaxValue() {

    when(user.getXp()).thenReturn(Long.MAX_VALUE - 10L);

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    assertThrows(
            IllegalArgumentException.class,
            () -> xpService.addXp(1L, 11L)
    );

  
    verify(userRepository).findById(1L);

    verify(userRepository, never())
            .save(any(User.class));

    verifyNoInteractions(notificationService);
}


    @Test
    void addXp_shouldAllowXpWhenTotalEqualsLongMaxValue() {
        prepareUserWithXp(Long.MAX_VALUE - 10L);

        User result = xpService.addXp(1L, 10L);

        assertSame(user, result);
        verify(user).setXp(Long.MAX_VALUE);
        verify(userRepository).save(user);
    }



    @Test
    void calculateChallengeXp_shouldReturn50ForEasy() {
        assertEquals(50L, xpService.calculateChallengeXp("EASY"));
    }

    @Test
    void calculateChallengeXp_shouldReturn100ForMedium() {
        assertEquals(100L, xpService.calculateChallengeXp("MEDIUM"));
    }

    @Test
    void calculateChallengeXp_shouldReturn150ForHard() {
        assertEquals(150L, xpService.calculateChallengeXp("HARD"));
    }

    @Test
    void calculateChallengeXp_shouldIgnoreCase() {
        assertEquals(50L, xpService.calculateChallengeXp("easy"));
        assertEquals(100L, xpService.calculateChallengeXp("Medium"));
        assertEquals(150L, xpService.calculateChallengeXp("hard"));
    }

    @Test
    void calculateChallengeXp_shouldTrimWhitespace() {
        assertEquals(
                100L,
                xpService.calculateChallengeXp("  MEDIUM  ")
        );
    }



    @Test
    void calculateChallengeXp_shouldThrowExceptionForNullDifficulty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.calculateChallengeXp(null)
        );
    }

    @Test
    void calculateChallengeXp_shouldThrowExceptionForBlankDifficulty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.calculateChallengeXp("   ")
        );
    }

    @Test
    void calculateChallengeXp_shouldThrowExceptionForInvalidDifficulty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.calculateChallengeXp("EXTREME")
        );
    }



    @Test
    void awardChallengeXp_shouldAwardEasyReward() {
        prepareUserWithXp(0L);

        User result = xpService.awardChallengeXp(1L, "EASY");

        assertSame(user, result);
        verify(user).setXp(50L);
        verify(userRepository).save(user);
    }

    @Test
    void awardChallengeXp_shouldAwardMediumReward() {
        prepareUserWithXp(0L);

        xpService.awardChallengeXp(1L, "MEDIUM");

        verify(user).setXp(100L);
        verify(userRepository).save(user);
    }

    @Test
    void awardChallengeXp_shouldAwardHardReward() {
        prepareUserWithXp(0L);

        xpService.awardChallengeXp(1L, "HARD");

        verify(user).setXp(150L);
        verify(userRepository).save(user);
    }

    @Test
    void awardChallengeXp_shouldRejectInvalidDifficultyBeforeDatabaseCall() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.awardChallengeXp(1L, "INVALID")
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(notificationService);
    }



    @Test
    void awardDailyQuestXp_shouldAwardDailyRewardSuccessfully() {
        prepareUserWithXp(100L);

        User result = xpService.awardDailyQuestXp(1L, 25L);

        assertSame(user, result);
        verify(user).setXp(125L);
        verify(userRepository).save(user);
    }


    @Test
    void awardDailyQuestXp_shouldRejectZeroReward() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.awardDailyQuestXp(1L, 0L)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void awardDailyQuestXp_shouldRejectNegativeReward() {
        assertThrows(
                IllegalArgumentException.class,
                () -> xpService.awardDailyQuestXp(1L, -10L)
        );

        verifyNoInteractions(userRepository);
    }



    @Test
    void calculateLevel_shouldReturnLevel1Below100Xp() {
        assertEquals(1, xpService.calculateLevel(0L));
        assertEquals(1, xpService.calculateLevel(99L));
    }

    @Test
    void calculateLevel_shouldReturnLevel2From100To249Xp() {
        assertEquals(2, xpService.calculateLevel(100L));
        assertEquals(2, xpService.calculateLevel(249L));
    }

    @Test
    void calculateLevel_shouldReturnLevel3From250To449Xp() {
        assertEquals(3, xpService.calculateLevel(250L));
        assertEquals(3, xpService.calculateLevel(449L));
    }

    @Test
    void calculateLevel_shouldReturnLevel4From450To699Xp() {
        assertEquals(4, xpService.calculateLevel(450L));
        assertEquals(4, xpService.calculateLevel(699L));
    }

    @Test
    void calculateLevel_shouldReturnLevel5From700To999Xp() {
        assertEquals(5, xpService.calculateLevel(700L));
        assertEquals(5, xpService.calculateLevel(999L));
    }

    @Test
    void calculateLevel_shouldReturnLevel6At1000Xp() {
        assertEquals(6, xpService.calculateLevel(1000L));
    }

    @Test
    void calculateLevel_shouldIncreaseLevelEvery500XpAfter1000() {
        assertEquals(6, xpService.calculateLevel(1499L));
        assertEquals(7, xpService.calculateLevel(1500L));
        assertEquals(8, xpService.calculateLevel(2000L));
    }

    @Test
    void calculateLevel_shouldCapLevelAtIntegerMaxValue() {
        assertEquals(
                Integer.MAX_VALUE,
                xpService.calculateLevel(Long.MAX_VALUE)
        );
    }
}
