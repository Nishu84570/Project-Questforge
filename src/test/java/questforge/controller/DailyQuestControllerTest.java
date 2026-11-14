
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

import questforge.dto.DailyQuestResponseDTO;
import questforge.model.DailyQuest;
import questforge.model.User;
import questforge.repository.UserRepository;
import questforge.service.DailyQuestService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyQuestControllerTest {

    @Mock
    private DailyQuestService dailyQuestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private User user;

    @Mock
    private DailyQuest dailyQuest;

    @Mock
    private DailyQuestResponseDTO responseDTO;

    @InjectMocks
    private DailyQuestController dailyQuestController;

    private final String EMAIL = "test@example.com";
    private final Long USER_ID = 101L;


    @Test
    void getTodayQuest_ShouldReturnQuest_WhenUserIsAuthenticated() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(user.getId()).thenReturn(USER_ID);

        when(dailyQuestService.getTodayQuest(USER_ID))
                .thenReturn(dailyQuest);

        when(dailyQuestService.convertToResponseDTO(dailyQuest))
                .thenReturn(responseDTO);

        ResponseEntity<DailyQuestResponseDTO> response =
                dailyQuestController.getTodayQuest(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(responseDTO, response.getBody());

        verify(userRepository).findByEmail(EMAIL);
        verify(dailyQuestService).getTodayQuest(USER_ID);
        verify(dailyQuestService).convertToResponseDTO(dailyQuest);
    }

 
    @Test
    void getTodayQuest_ShouldThrowUnauthorized_WhenAuthenticationIsNull() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> dailyQuestController.getTodayQuest(null)
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatusCode()
        );

        assertEquals("Authentication required", exception.getReason());

        verifyNoInteractions(userRepository, dailyQuestService);
    }

    @Test
    void getTodayQuest_ShouldThrowUnauthorized_WhenUserIsNotAuthenticated() {

        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> dailyQuestController.getTodayQuest(authentication)
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatusCode()
        );

        assertEquals("Authentication required", exception.getReason());

        verifyNoInteractions(userRepository, dailyQuestService);
    }

    @Test
    void getTodayQuest_ShouldThrowNotFound_WhenUserDoesNotExist() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> dailyQuestController.getTodayQuest(authentication)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        assertEquals("User not found", exception.getReason());

        verify(userRepository).findByEmail(EMAIL);
        verifyNoInteractions(dailyQuestService);
    }


    @Test
    void getQuestHistory_ShouldReturnHistory_WhenUserIsAuthenticated() {

        List<DailyQuestResponseDTO> history = Arrays.asList(
                mock(DailyQuestResponseDTO.class),
                mock(DailyQuestResponseDTO.class)
        );

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(user.getId()).thenReturn(USER_ID);

        when(dailyQuestService.getQuestHistory(USER_ID))
                .thenReturn(history);

        ResponseEntity<List<DailyQuestResponseDTO>> response =
                dailyQuestController.getQuestHistory(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertSame(history, response.getBody());

        verify(dailyQuestService).getQuestHistory(USER_ID);
    }


    @Test
    void getQuestHistory_ShouldReturnEmptyList_WhenNoHistoryExists() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(user.getId()).thenReturn(USER_ID);

        when(dailyQuestService.getQuestHistory(USER_ID))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<DailyQuestResponseDTO>> response =
                dailyQuestController.getQuestHistory(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(dailyQuestService).getQuestHistory(USER_ID);
    }


    @Test
    void completeDailyQuest_ShouldReturnCompletedQuest_WhenUserIsAuthenticated() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(user.getId()).thenReturn(USER_ID);

        when(dailyQuestService.recordChallengeCompletion(USER_ID))
                .thenReturn(dailyQuest);

        when(dailyQuestService.convertToResponseDTO(dailyQuest))
                .thenReturn(responseDTO);

        ResponseEntity<DailyQuestResponseDTO> response =
                dailyQuestController.completeDailyQuest(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(responseDTO, response.getBody());

        verify(dailyQuestService)
                .recordChallengeCompletion(USER_ID);

        verify(dailyQuestService)
                .convertToResponseDTO(dailyQuest);
    }


    @Test
    void completeDailyQuest_ShouldThrowUnauthorized_WhenAuthenticationIsNull() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> dailyQuestController.completeDailyQuest(null)
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatusCode()
        );

        verifyNoInteractions(userRepository, dailyQuestService);
    }


    @Test
    void completeDailyQuest_ShouldThrowNotFound_WhenUserDoesNotExist() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(EMAIL);

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> dailyQuestController.completeDailyQuest(authentication)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(userRepository).findByEmail(EMAIL);
        verifyNoInteractions(dailyQuestService);
    }


    @Test
    void getQuestHistory_ShouldThrowUnauthorized_WhenAuthenticationIsNull() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> dailyQuestController.getQuestHistory(null)
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatusCode()
        );

        verifyNoInteractions(userRepository, dailyQuestService);
    }
}
