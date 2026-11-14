
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.ChallengeRequestDTO;
import questforge.dto.ChallengeResponseDTO;
import questforge.service.ChallengeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeControllerTest {

    @Mock
    private ChallengeService challengeService;

    @Mock
    private ChallengeRequestDTO request;

    @InjectMocks
    private ChallengeController challengeController;

    private final Long CHALLENGE_ID = 101L;

    @Test
    void createChallenge_ShouldReturnCreatedChallenge() {

        ChallengeResponseDTO challenge =
                mock(ChallengeResponseDTO.class);

        when(challengeService.createChallenge(request))
                .thenReturn(challenge);

        ResponseEntity<ChallengeResponseDTO> response =
                challengeController.createChallenge(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(challenge, response.getBody());

        verify(challengeService).createChallenge(request);
    }


    @Test
    void getAllChallenges_ShouldReturnAllChallenges() {

        List<ChallengeResponseDTO> challenges = Arrays.asList(
                mock(ChallengeResponseDTO.class),
                mock(ChallengeResponseDTO.class)
        );

        when(challengeService.getAllChallenges())
                .thenReturn(challenges);

        ResponseEntity<List<ChallengeResponseDTO>> response =
                challengeController.getAllChallenges();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(challenges, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(challengeService).getAllChallenges();
    }


    @Test
    void getAllChallenges_ShouldReturnEmptyList_WhenNoChallengesExist() {

        when(challengeService.getAllChallenges())
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<ChallengeResponseDTO>> response =
                challengeController.getAllChallenges();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(challengeService).getAllChallenges();
    }

    @Test
    void getChallengeById_ShouldReturnChallenge_WhenChallengeExists() {

        ChallengeResponseDTO challenge =
                mock(ChallengeResponseDTO.class);

        when(challengeService.getChallengeById(CHALLENGE_ID))
                .thenReturn(challenge);

        ResponseEntity<ChallengeResponseDTO> response =
                challengeController.getChallengeById(CHALLENGE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(challenge, response.getBody());

        verify(challengeService).getChallengeById(CHALLENGE_ID);
    }

  
    @Test
    void updateChallenge_ShouldReturnUpdatedChallenge() {

        ChallengeResponseDTO updatedChallenge =
                mock(ChallengeResponseDTO.class);

        when(challengeService.updateChallenge(CHALLENGE_ID, request))
                .thenReturn(updatedChallenge);

        ResponseEntity<ChallengeResponseDTO> response =
                challengeController.updateChallenge(
                        CHALLENGE_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedChallenge, response.getBody());

        verify(challengeService)
                .updateChallenge(CHALLENGE_ID, request);
    }


    @Test
    void deleteChallenge_ShouldReturnNoContent_WhenDeletionSucceeds() {

        doNothing().when(challengeService).deleteChallenge(CHALLENGE_ID);

        ResponseEntity<Void> response =
                challengeController.deleteChallenge(CHALLENGE_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(challengeService, times(1))
                .deleteChallenge(CHALLENGE_ID);
    }

  
    @Test
    void createChallenge_ShouldPropagateException_WhenServiceFails() {

        when(challengeService.createChallenge(request))
                .thenThrow(new RuntimeException("Challenge creation failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> challengeController.createChallenge(request)
        );

        assertEquals("Challenge creation failed", exception.getMessage());

        verify(challengeService).createChallenge(request);
    }

    
    @Test
    void deleteChallenge_ShouldPropagateException_WhenServiceFails() {

        doThrow(new RuntimeException("Challenge deletion failed"))
                .when(challengeService).deleteChallenge(CHALLENGE_ID);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> challengeController.deleteChallenge(CHALLENGE_ID)
        );

        assertEquals("Challenge deletion failed", exception.getMessage());

        verify(challengeService).deleteChallenge(CHALLENGE_ID);
    }
}
