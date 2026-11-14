
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import questforge.dto.AttemptRequestDTO;
import questforge.dto.AttemptResponseDTO;
import questforge.service.AttemptService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttemptControllerTest {

    @Mock
    private AttemptService attemptService;

    @Mock
    private AttemptRequestDTO request;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AttemptController attemptController;

    private final Long ATTEMPT_ID = 101L;
    private final Long USER_ID = 201L;
    private final Long CHALLENGE_ID = 301L;

  
    @Test
    void createAttempt_ShouldReturnCreatedAttempt() {

        AttemptResponseDTO attempt =
                mock(AttemptResponseDTO.class);

        when(attemptService.createAttempt(request))
                .thenReturn(attempt);

        ResponseEntity<AttemptResponseDTO> response =
                attemptController.createAttempt(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(attempt, response.getBody());

        verify(attemptService).createAttempt(request);
    }


    @Test
    void getAllAttempts_ShouldReturnAllAttempts() {

        List<AttemptResponseDTO> attempts = Arrays.asList(
                mock(AttemptResponseDTO.class),
                mock(AttemptResponseDTO.class)
        );

        when(attemptService.getAllAttempts())
                .thenReturn(attempts);

        ResponseEntity<List<AttemptResponseDTO>> response =
                attemptController.getAllAttempts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(attempts, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(attemptService).getAllAttempts();
    }

  
    @Test
    void getAllAttempts_ShouldReturnEmptyList_WhenNoAttemptsExist() {

        when(attemptService.getAllAttempts())
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<AttemptResponseDTO>> response =
                attemptController.getAllAttempts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(attemptService).getAllAttempts();
    }

    @Test
    void getAttemptById_ShouldReturnAttempt_WhenAttemptExists() {

        AttemptResponseDTO attempt =
                mock(AttemptResponseDTO.class);

        when(attemptService.getAttemptById(ATTEMPT_ID))
                .thenReturn(attempt);

        ResponseEntity<AttemptResponseDTO> response =
                attemptController.getAttemptById(
                        ATTEMPT_ID, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(attempt, response.getBody());

        verify(attemptService).getAttemptById(ATTEMPT_ID);
    }


    @Test
    void getAttemptsByUser_ShouldReturnUserAttempts() {

        List<AttemptResponseDTO> attempts = Collections.singletonList(
                mock(AttemptResponseDTO.class)
        );

        when(attemptService.getAttemptsByUser(USER_ID))
                .thenReturn(attempts);

        ResponseEntity<List<AttemptResponseDTO>> response =
                attemptController.getAttemptsByUser(
                        USER_ID, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(attempts, response.getBody());

        verify(attemptService).getAttemptsByUser(USER_ID);
    }


    @Test
    void getAttemptsByChallenge_ShouldReturnChallengeAttempts() {

        List<AttemptResponseDTO> attempts = Arrays.asList(
                mock(AttemptResponseDTO.class),
                mock(AttemptResponseDTO.class)
        );

        when(attemptService.getAttemptsByChallenge(CHALLENGE_ID))
                .thenReturn(attempts);

        ResponseEntity<List<AttemptResponseDTO>> response =
                attemptController.getAttemptsByChallenge(
                        CHALLENGE_ID, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(attempts, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(attemptService)
                .getAttemptsByChallenge(CHALLENGE_ID);
    }

  
    @Test
    void completeAttempt_ShouldReturnCompletedAttempt() {

        AttemptResponseDTO completedAttempt =
                mock(AttemptResponseDTO.class);

        when(attemptService.completeAttempt(ATTEMPT_ID))
                .thenReturn(completedAttempt);

        ResponseEntity<AttemptResponseDTO> response =
                attemptController.completeAttempt(
                        ATTEMPT_ID, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(completedAttempt, response.getBody());

        verify(attemptService).completeAttempt(ATTEMPT_ID);
    }

  
    @Test
    void deleteAttempt_ShouldReturnNoContent_WhenDeletionSucceeds() {

        doNothing().when(attemptService).deleteAttempt(ATTEMPT_ID);

        ResponseEntity<Void> response =
                attemptController.deleteAttempt(ATTEMPT_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(attemptService, times(1))
                .deleteAttempt(ATTEMPT_ID);
    }


    @Test
    void createAttempt_ShouldPropagateException_WhenServiceFails() {

        when(attemptService.createAttempt(request))
                .thenThrow(new RuntimeException("Attempt creation failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> attemptController.createAttempt(
                        request, authentication)
        );

        assertEquals("Attempt creation failed", exception.getMessage());

        verify(attemptService).createAttempt(request);
    }

   
    @Test
    void completeAttempt_ShouldPropagateException_WhenServiceFails() {

        when(attemptService.completeAttempt(ATTEMPT_ID))
                .thenThrow(new RuntimeException("Attempt completion failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> attemptController.completeAttempt(
                        ATTEMPT_ID, authentication)
        );

        assertEquals("Attempt completion failed", exception.getMessage());

        verify(attemptService).completeAttempt(ATTEMPT_ID);
    }


    @Test
    void deleteAttempt_ShouldPropagateException_WhenServiceFails() {

        doThrow(new RuntimeException("Attempt deletion failed"))
                .when(attemptService).deleteAttempt(ATTEMPT_ID);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> attemptController.deleteAttempt(ATTEMPT_ID)
        );

        assertEquals("Attempt deletion failed", exception.getMessage());

        verify(attemptService).deleteAttempt(ATTEMPT_ID);
    }
}
