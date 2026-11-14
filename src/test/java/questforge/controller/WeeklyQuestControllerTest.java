
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import questforge.dto.WeeklyQuestResponseDTO;
import questforge.model.User;
import questforge.model.WeeklyQuest;
import questforge.repository.UserRepository;
import questforge.service.WeeklyQuestService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeeklyQuestControllerTest {

    @Mock
    private WeeklyQuestService weeklyQuestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private WeeklyQuestController weeklyQuestController;

    @Test
    void getCurrentWeekQuest_ShouldReturnCurrentQuest() {

        Long userId = 1L;
        String email = "test@gmail.com";

        User user = mock(User.class);
        WeeklyQuest quest = mock(WeeklyQuest.class);
        WeeklyQuestResponseDTO expected =
                mock(WeeklyQuestResponseDTO.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(user.getId()).thenReturn(userId);

        when(weeklyQuestService.getCurrentWeekQuest(userId))
                .thenReturn(quest);

        when(weeklyQuestService.convertToResponseDTO(quest))
                .thenReturn(expected);

        ResponseEntity<WeeklyQuestResponseDTO> response =
                weeklyQuestController.getCurrentWeekQuest(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userRepository).findByEmail(email);
        verify(weeklyQuestService).getCurrentWeekQuest(userId);
        verify(weeklyQuestService).convertToResponseDTO(quest);
    }

    @Test
    void getQuestHistory_ShouldReturnQuestHistory() {

        Long userId = 1L;
        String email = "test@gmail.com";

        User user = mock(User.class);

        List<WeeklyQuestResponseDTO> expected =
                List.of(
                        mock(WeeklyQuestResponseDTO.class),
                        mock(WeeklyQuestResponseDTO.class)
                );

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(user.getId()).thenReturn(userId);

        when(weeklyQuestService.getQuestHistory(userId))
                .thenReturn(expected);

        ResponseEntity<List<WeeklyQuestResponseDTO>> response =
                weeklyQuestController.getQuestHistory(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userRepository).findByEmail(email);
        verify(weeklyQuestService).getQuestHistory(userId);
    }

    @Test
    void getCurrentWeekQuest_ShouldThrowUnauthorized_WhenAuthenticationIsNull() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> weeklyQuestController.getCurrentWeekQuest(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verifyNoInteractions(userRepository, weeklyQuestService);
    }

    @Test
    void getCurrentWeekQuest_ShouldThrowUnauthorized_WhenNotAuthenticated() {

        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> weeklyQuestController.getCurrentWeekQuest(authentication)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verifyNoInteractions(userRepository, weeklyQuestService);
    }

    @Test
    void getCurrentWeekQuest_ShouldThrowNotFound_WhenUserDoesNotExist() {

        String email = "unknown@gmail.com";

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> weeklyQuestController.getCurrentWeekQuest(authentication)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(weeklyQuestService);
    }

    @Test
    void getQuestHistory_ShouldThrowUnauthorized_WhenAuthenticationIsNull() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> weeklyQuestController.getQuestHistory(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verifyNoInteractions(userRepository, weeklyQuestService);
    }

    @Test
    void getQuestHistory_ShouldThrowNotFound_WhenUserDoesNotExist() {

        String email = "unknown@gmail.com";

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> weeklyQuestController.getQuestHistory(authentication)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(weeklyQuestService);
    }
}
