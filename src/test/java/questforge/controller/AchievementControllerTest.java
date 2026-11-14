
package questforge.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import questforge.dto.AchievementResponseDTO;
import questforge.service.AchievementService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementControllerTest {

    @Mock
    private AchievementService achievementService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AchievementController achievementController;

    private final String EMAIL = "test@example.com";

    private List<AchievementResponseDTO> achievements;

    @BeforeEach
    void setUp() {
        achievements = Arrays.asList(
                mock(AchievementResponseDTO.class),
                mock(AchievementResponseDTO.class)
        );
    }

    @Test
    void getMyAchievements_ShouldReturnAchievements_WhenAuthenticated() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL);

        when(achievementService.getUserAchievements(EMAIL))
                .thenReturn(achievements);

        ResponseEntity<List<AchievementResponseDTO>> response =
                achievementController.getMyAchievements(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertSame(achievements, response.getBody());

        verify(achievementService).getUserAchievements(EMAIL);
    }

    @Test
    void getMyAchievements_ShouldReturnUnauthorized_WhenAuthenticationIsNull() {

        ResponseEntity<List<AchievementResponseDTO>> response =
                achievementController.getMyAchievements(null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());

        verifyNoInteractions(achievementService);
    }

    @Test
    void getMyAchievements_ShouldReturnUnauthorized_WhenUserIsNotAuthenticated() {

        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<List<AchievementResponseDTO>> response =
                achievementController.getMyAchievements(authentication);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());

        verifyNoInteractions(achievementService);
    }


    @Test
    void getMyAchievements_ShouldReturnEmptyList_WhenNoAchievementsExist() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL);

        when(achievementService.getUserAchievements(EMAIL))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<AchievementResponseDTO>> response =
                achievementController.getMyAchievements(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(achievementService).getUserAchievements(EMAIL);
    }


    @Test
    void getMyAchievements_ShouldPassAuthenticatedUserEmailToService() {

        String anotherEmail = "admin@example.com";

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(anotherEmail);

        when(achievementService.getUserAchievements(anotherEmail))
                .thenReturn(Collections.emptyList());

        achievementController.getMyAchievements(authentication);

        verify(achievementService, times(1))
                .getUserAchievements(anotherEmail);
    }
}
