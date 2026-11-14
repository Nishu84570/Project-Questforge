
package questforge.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import questforge.dto.WeeklyQuestResponseDTO;
import questforge.model.User;
import questforge.model.WeeklyQuest;
import questforge.repository.UserRepository;
import questforge.repository.WeeklyQuestRepository;
import questforge.service.NotificationService;
import questforge.service.WeeklyQuestService;
import questforge.service.XPService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeeklyQuestServiceTest {

    @Mock
    private WeeklyQuestRepository weeklyQuestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private XPService xpService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private WeeklyQuestService weeklyQuestService;

    private Long userId;
    private LocalDate weekStart;

    @BeforeEach
    void setUp() {
        userId = 1L;
        weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
    }

    private WeeklyQuest createQuest(
            Long progress,
            Long target,
            Boolean completed) {

        WeeklyQuest quest = new WeeklyQuest();

        quest.setUserId(101L);
        quest.setUserId(userId);
        quest.setWeekStart(weekStart);
        quest.setTitle("Complete 5 Challenges");
        quest.setTarget(target);
        quest.setProgress(progress);
        quest.setCompleted(completed);
        quest.setRewardXp(150L);

        return quest;
    }

    @Test
    void generateWeeklyQuests_ShouldCreateQuestForAllUsers() {

        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(user1.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        when(weeklyQuestRepository
                .findByUserIdAndWeekStart(anyLong(), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        when(weeklyQuestRepository.save(any(WeeklyQuest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        weeklyQuestService.generateWeeklyQuests();

        verify(userRepository).findAll();

        verify(weeklyQuestRepository, times(2))
                .save(any(WeeklyQuest.class));
    }


    @Test
    void getCurrentWeekQuest_ShouldCreateNewQuest_WhenNotExists() {

        when(weeklyQuestRepository
                .findByUserIdAndWeekStart(userId, weekStart))
                .thenReturn(Optional.empty());

        when(weeklyQuestRepository.save(any(WeeklyQuest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WeeklyQuest result =
                weeklyQuestService.getCurrentWeekQuest(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(weekStart, result.getWeekStart());
        assertEquals("Complete 5 Challenges", result.getTitle());
        assertEquals(5L, result.getTarget());
        assertEquals(0L, result.getProgress());
        assertFalse(result.getCompleted());
        assertEquals(150L, result.getRewardXp());

        verify(weeklyQuestRepository).save(any(WeeklyQuest.class));
    }

    @Test
    void getCurrentWeekQuest_ShouldReturnExistingQuest() {

        WeeklyQuest existingQuest = createQuest(2L, 5L, false);

        when(weeklyQuestRepository
                .findByUserIdAndWeekStart(userId, weekStart))
                .thenReturn(Optional.of(existingQuest));

        WeeklyQuest result =
                weeklyQuestService.getCurrentWeekQuest(userId);

        assertSame(existingQuest, result);

        verify(weeklyQuestRepository, never())
                .save(any(WeeklyQuest.class));
    }

    @Test
    void recordChallengeCompletion_ShouldIncrementProgress() {

        WeeklyQuest quest = createQuest(2L, 5L, false);

        when(weeklyQuestRepository
                .findByUserIdAndWeekStart(userId, weekStart))
                .thenReturn(Optional.of(quest));

        when(weeklyQuestRepository.save(any(WeeklyQuest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WeeklyQuest result =
                weeklyQuestService.recordChallengeCompletion(userId);

        assertEquals(3L, result.getProgress());
        assertFalse(result.getCompleted());

        verify(weeklyQuestRepository).save(quest);

        verifyNoInteractions(xpService, notificationService);
    }

    @Test
    void recordChallengeCompletion_ShouldAwardXp_WhenTargetReached() {

        WeeklyQuest quest = createQuest(4L, 5L, false);

        when(weeklyQuestRepository
                .findByUserIdAndWeekStart(userId, weekStart))
                .thenReturn(Optional.of(quest));

        when(weeklyQuestRepository.save(any(WeeklyQuest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WeeklyQuest result =
                weeklyQuestService.recordChallengeCompletion(userId);

        assertEquals(5L, result.getProgress());
        assertTrue(result.getCompleted());

        verify(xpService).awardDailyQuestXp(userId, 150L);

        verify(notificationService).createNotification(
                eq(userId),
                eq("Weekly Quest Completed!"),
                contains("150 XP"),
                eq("QUEST")
        );

        verify(weeklyQuestRepository).save(quest);
    }

    @Test
    void recordChallengeCompletion_ShouldNotIncrement_WhenAlreadyCompleted() {

        WeeklyQuest quest = createQuest(5L, 5L, true);

        when(weeklyQuestRepository
                .findByUserIdAndWeekStart(userId, weekStart))
                .thenReturn(Optional.of(quest));

        WeeklyQuest result =
                weeklyQuestService.recordChallengeCompletion(userId);

        assertSame(quest, result);
        assertEquals(5L, result.getProgress());
        assertTrue(result.getCompleted());

        verify(weeklyQuestRepository, never())
                .save(any(WeeklyQuest.class));

        verifyNoInteractions(xpService, notificationService);
    }

    @Test
    void getQuestHistory_ShouldReturnQuestHistory() {

        WeeklyQuest quest = createQuest(3L, 5L, false);

        when(weeklyQuestRepository
                .findByUserIdOrderByWeekStartDesc(userId))
                .thenReturn(List.of(quest));

        List<WeeklyQuestResponseDTO> result =
                weeklyQuestService.getQuestHistory(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0));

        verify(weeklyQuestRepository)
                .findByUserIdOrderByWeekStartDesc(userId);
    }


    @Test
    void convertToResponseDTO_ShouldReturnCorrectDTO() {

        WeeklyQuest quest = createQuest(3L, 5L, false);

        WeeklyQuestResponseDTO result =
                weeklyQuestService.convertToResponseDTO(quest);

        assertNotNull(result);
    }


    @Test
    void getCurrentWeekQuest_ShouldThrowException_WhenUserIdIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> weeklyQuestService.getCurrentWeekQuest(null)
        );

        verifyNoInteractions(weeklyQuestRepository);
    }


    @Test
    void recordChallengeCompletion_ShouldThrowException_WhenUserIdIsZero() {

        assertThrows(
                IllegalArgumentException.class,
                () -> weeklyQuestService.recordChallengeCompletion(0L)
        );

        verifyNoInteractions(weeklyQuestRepository);
    }


    @Test
    void getQuestHistory_ShouldThrowException_WhenUserIdIsNegative() {

        assertThrows(
                IllegalArgumentException.class,
                () -> weeklyQuestService.getQuestHistory(-1L)
        );

        verifyNoInteractions(weeklyQuestRepository);
    }
}
