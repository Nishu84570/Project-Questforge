
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

import questforge.model.User;
import questforge.model.UserStreak;
import questforge.repository.UserRepository;
import questforge.service.StreakService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreakControllerTest {

    @Mock
    private StreakService streakService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StreakController streakController;

    @Test
    void getMyStreak_ShouldReturnUserStreak() {

        Long userId = 1L;
        User user = mock(User.class);
        UserStreak expected = mock(UserStreak.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(user.getId()).thenReturn(userId);

        when(streakService.getUserStreak(userId))
                .thenReturn(expected);

        ResponseEntity<UserStreak> response =
                streakController.getMyStreak(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userRepository).findByEmail("test@gmail.com");
        verify(streakService).getUserStreak(userId);
    }

    @Test
    void getMyStreak_ShouldThrowUnauthorized_WhenAuthenticationIsNull() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> streakController.getMyStreak(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verifyNoInteractions(userRepository, streakService);
    }

    @Test
    void getMyStreak_ShouldThrowUnauthorized_WhenUserIsNotAuthenticated() {

        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> streakController.getMyStreak(authentication)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        verifyNoInteractions(userRepository, streakService);
    }

    @Test
    void getMyStreak_ShouldThrowNotFound_WhenUserDoesNotExist() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("unknown@gmail.com");

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> streakController.getMyStreak(authentication)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(userRepository).findByEmail("unknown@gmail.com");
        verifyNoInteractions(streakService);
    }
}
