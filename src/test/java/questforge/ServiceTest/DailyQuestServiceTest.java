
package questforge.ServiceTest;

import questforge.dto.DailyQuestResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.DailyQuest;
import questforge.model.User;
import questforge.repository.DailyQuestRepository;
import questforge.repository.UserRepository;
import questforge.service.DailyQuestService;
import questforge.service.NotificationService;
import questforge.service.XPService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyQuestServiceTest {

    @Mock
    private DailyQuestRepository dailyQuestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private XPService xpService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private DailyQuestService dailyQuestService;

    private User user;

    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();

        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    private DailyQuest createQuest(
            Long id,
            Long userId,
            LocalDate date,
            long progress,
            boolean completed) {

        DailyQuest quest = new DailyQuest();
        ReflectionTestUtils.setField(quest, "id", id);

        quest.setUserId(userId);
        quest.setQuestDate(date);
        quest.setTitle("Complete 1 Challenge");
        quest.setTarget(1L);
        quest.setProgress(progress);
        quest.setCompleted(completed);
        quest.setRewardXp(25L);

        return quest;
    }

    @Test
    void generateDailyQuests_Success() {

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        when(dailyQuestRepository.findByUserIdAndQuestDate(
                1L, today))
                .thenReturn(Optional.empty());

        when(dailyQuestRepository.save(any(DailyQuest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        dailyQuestService.generateDailyQuests();

        verify(userRepository).findAll();

        verify(dailyQuestRepository)
                .findByUserIdAndQuestDate(1L, today);

        verify(dailyQuestRepository).save(any(DailyQuest.class));
    }

    @Test
    void generateDailyQuests_QuestAlreadyExists() {

        DailyQuest existingQuest =
                createQuest(1L, 1L, today, 0L, false);

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        when(dailyQuestRepository.findByUserIdAndQuestDate(
                1L, today))
                .thenReturn(Optional.of(existingQuest));

        dailyQuestService.generateDailyQuests();

        verify(dailyQuestRepository)
                .findByUserIdAndQuestDate(1L, today);

        verify(dailyQuestRepository, never())
                .save(any(DailyQuest.class));
    }

    @Test
    void recordChallengeCompletion_Success() {

        DailyQuest quest =
                createQuest(1L, 1L, today, 0L, false);

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(dailyQuestRepository.findByUserIdAndQuestDate(
                1L, today))
                .thenReturn(Optional.of(quest));

        when(dailyQuestRepository.save(any(DailyQuest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DailyQuest result =
                dailyQuestService.recordChallengeCompletion(1L);

        assertNotNull(result);

        assertEquals(1L, result.getProgress());
        assertTrue(result.getCompleted());
        assertEquals(25L, result.getRewardXp());

        verify(xpService).awardDailyQuestXp(1L, 25L);

        verify(notificationService).createNotification(
                1L,
                "Daily Quest Completed!",
                "You completed your daily quest and earned 25 XP.",
                "QUEST"
        );

        verify(dailyQuestRepository).save(quest);
    }

    @Test
    void recordChallengeCompletion_AlreadyCompleted() {

        DailyQuest quest =
                createQuest(1L, 1L, today, 1L, true);

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(dailyQuestRepository.findByUserIdAndQuestDate(
                1L, today))
                .thenReturn(Optional.of(quest));

        DailyQuest result =
                dailyQuestService.recordChallengeCompletion(1L);

        assertNotNull(result);
        assertTrue(result.getCompleted());
        assertEquals(1L, result.getProgress());

        verify(dailyQuestRepository, never())
                .save(any(DailyQuest.class));

        verifyNoInteractions(xpService, notificationService);
    }


    @Test
    void recordChallengeCompletion_CreateNewQuest() {

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(dailyQuestRepository.findByUserIdAndQuestDate(
                1L, today))
                .thenReturn(Optional.empty());

        when(dailyQuestRepository.save(any(DailyQuest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DailyQuest result =
                dailyQuestService.recordChallengeCompletion(1L);

        assertNotNull(result);

        assertEquals(1L, result.getUserId());
        assertEquals(today, result.getQuestDate());
        assertEquals("Complete 1 Challenge", result.getTitle());
        assertEquals(1L, result.getTarget());
        assertEquals(1L, result.getProgress());
        assertTrue(result.getCompleted());
        assertEquals(25L, result.getRewardXp());

        verify(xpService).awardDailyQuestXp(1L, 25L);

        verify(notificationService).createNotification(
                1L,
                "Daily Quest Completed!",
                "You completed your daily quest and earned 25 XP.",
                "QUEST"
        );
    }

    @Test
    void recordChallengeCompletion_UserNotFound() {

        when(userRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> dailyQuestService.recordChallengeCompletion(99L)
        );

        verify(dailyQuestRepository, never())
                .save(any(DailyQuest.class));

        verifyNoInteractions(xpService, notificationService);
    }

    @Test
    void getTodayQuest_Success() {

        DailyQuest quest =
                createQuest(1L, 1L, today, 0L, false);

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(dailyQuestRepository.findByUserIdAndQuestDate(
                1L, today))
                .thenReturn(Optional.of(quest));

        DailyQuest result =
                dailyQuestService.getTodayQuest(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Complete 1 Challenge", result.getTitle());
        assertEquals(0L, result.getProgress());
        assertFalse(result.getCompleted());

        verify(dailyQuestRepository, never())
                .save(any(DailyQuest.class));
    }

    @Test
    void getTodayQuest_UserNotFound() {

        when(userRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> dailyQuestService.getTodayQuest(99L)
        );

        verifyNoInteractions(dailyQuestRepository);
    }

    @Test
    void getQuestHistory_Success() {

        DailyQuest quest =
                createQuest(1L, 1L, today, 1L, true);

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(dailyQuestRepository
                .findByUserIdOrderByQuestDateDesc(1L))
                .thenReturn(List.of(quest));

        List<DailyQuestResponseDTO> result =
                dailyQuestService.getQuestHistory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(today, result.get(0).getQuestDate());
        assertEquals(
                "Complete 1 Challenge",
                result.get(0).getTitle()
        );

        verify(dailyQuestRepository)
                .findByUserIdOrderByQuestDateDesc(1L);
    }


    @Test
    void getQuestHistory_EmptyList() {

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(dailyQuestRepository
                .findByUserIdOrderByQuestDateDesc(1L))
                .thenReturn(List.of());

        List<DailyQuestResponseDTO> result =
                dailyQuestService.getQuestHistory(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void getQuestHistory_UserNotFound() {

        when(userRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> dailyQuestService.getQuestHistory(99L)
        );

        verifyNoInteractions(dailyQuestRepository);
    }

    @Test
    void convertToResponseDTO_Success() {

        DailyQuest quest =
                createQuest(1L, 1L, today, 1L, true);

        DailyQuestResponseDTO result =
                dailyQuestService.convertToResponseDTO(quest);

        assertNotNull(result);

        assertEquals(1L, result.getId());
        assertEquals(today, result.getQuestDate());
        assertEquals("Complete 1 Challenge", result.getTitle());
        assertEquals(1L, result.getTarget());
        assertEquals(1L, result.getProgress());
        assertTrue(result.getCompleted());
        assertEquals(25L, result.getRewardXp());
    }
}
