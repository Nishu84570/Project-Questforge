
package questforge.ServiceTest;

import questforge.dto.AchievementResponseDTO;
import questforge.model.Achievement;
import questforge.model.User;
import questforge.repository.AchievementRepository;
import questforge.repository.UserRepository;
import questforge.service.AchievementService;
import questforge.service.NotificationService;
import questforge.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AchievementService achievementService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();

        ReflectionTestUtils.setField(user, "id", 1L);
    }

    private Achievement createAchievement(Long id) {

        Achievement achievement = new Achievement();
        ReflectionTestUtils.setField(achievement, "id", id);

        achievement.setUserId(1L);
        achievement.setCode("FIRST_LOGIN");
        achievement.setName("First Login");
        achievement.setDescription("Logged in for the first time");
        achievement.setIcon("trophy");

        return achievement;
    }

    @Test
    void getUserAchievements_Success() {

        Achievement achievement = createAchievement(1L);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(achievementRepository.findByUserId(1L))
                .thenReturn(List.of(achievement));

        List<AchievementResponseDTO> result =
                achievementService.getUserAchievements("test@gmail.com");

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("FIRST_LOGIN", result.get(0).getCode());
        assertEquals("First Login", result.get(0).getName());
        assertEquals(
                "Logged in for the first time",
                result.get(0).getDescription()
        );
        assertEquals("trophy", result.get(0).getIcon());

        verify(userRepository).findByEmail("test@gmail.com");
        verify(achievementRepository).findByUserId(1L);
    }

    @Test
    void getUserAchievements_EmptyList() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(achievementRepository.findByUserId(1L))
                .thenReturn(List.of());

        List<AchievementResponseDTO> result =
                achievementService.getUserAchievements("test@gmail.com");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(achievementRepository).findByUserId(1L);
    }


    @Test
    void getUserAchievements_UserNotFound() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> achievementService.getUserAchievements(
                        "unknown@gmail.com"
                )
        );

        verify(achievementRepository, never()).findByUserId(anyLong());
    }

    @Test
    void unlockAchievement_Success() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(achievementRepository.findByUserIdAndCode(
                1L, "FIRST_LOGIN"
        )).thenReturn(Optional.empty());


        when(achievementRepository.save(any(Achievement.class)))
                .thenAnswer(invocation -> {

                    Achievement saved = invocation.getArgument(0);

                    ReflectionTestUtils.setField(saved, "id", 1L);

                    return saved;
                });

        AchievementResponseDTO result =
                achievementService.unlockAchievement(
                        1L,
                        "FIRST_LOGIN",
                        "First Login",
                        "Logged in for the first time",
                        "trophy"
                );

        assertNotNull(result);

        assertEquals(1L, result.getId());
        assertEquals("FIRST_LOGIN", result.getCode());
        assertEquals("First Login", result.getName());
        assertEquals(
                "Logged in for the first time",
                result.getDescription()
        );
        assertEquals("trophy", result.getIcon());

        ArgumentCaptor<Achievement> captor =
                ArgumentCaptor.forClass(Achievement.class);

        verify(achievementRepository).save(captor.capture());

        Achievement savedAchievement = captor.getValue();

        assertEquals(1L, savedAchievement.getUserId());
        assertEquals("FIRST_LOGIN", savedAchievement.getCode());
        assertEquals("First Login", savedAchievement.getName());
        assertEquals(
                "Logged in for the first time",
                savedAchievement.getDescription()
        );
        assertEquals("trophy", savedAchievement.getIcon());

        verify(notificationService).createNotification(
                1L,
                "Achievement Unlocked!",
                "You unlocked the achievement: First Login",
                "ACHIEVEMENT"
        );
    }

    
    @Test
    void unlockAchievement_AlreadyExists() {

        Achievement existingAchievement = createAchievement(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(achievementRepository.findByUserIdAndCode(
                1L, "FIRST_LOGIN"
        )).thenReturn(Optional.of(existingAchievement));

        AchievementResponseDTO result =
                achievementService.unlockAchievement(
                        1L,
                        "FIRST_LOGIN",
                        "First Login",
                        "Logged in for the first time",
                        "trophy"
                );

        assertNotNull(result);

        assertEquals(1L, result.getId());
        assertEquals("FIRST_LOGIN", result.getCode());
        assertEquals("First Login", result.getName());

        verify(achievementRepository, never()).save(any(Achievement.class));

        verify(notificationService, never()).createNotification(
                anyLong(),
                anyString(),
                anyString(),
                anyString()
        );
    }


    @Test
    void unlockAchievement_NullUserId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> achievementService.unlockAchievement(
                        null,
                        "FIRST_LOGIN",
                        "First Login",
                        "Logged in for the first time",
                        "trophy"
                )
        );

        verifyNoInteractions(
                userRepository,
                achievementRepository,
                notificationService
        );
    }


    @Test
    void unlockAchievement_UserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> achievementService.unlockAchievement(
                        99L,
                        "FIRST_LOGIN",
                        "First Login",
                        "Logged in for the first time",
                        "trophy"
                )
        );

        verify(achievementRepository, never()).save(any(Achievement.class));

        verifyNoInteractions(notificationService);
    }
}
