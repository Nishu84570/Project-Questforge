
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.GlobalLeaderboardEntryDTO;
import questforge.dto.LeaderboardEntryDTO;
import questforge.service.LeaderboardService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardControllerTest {

    @Mock
    private LeaderboardService leaderboardService;

    @InjectMocks
    private LeaderboardController leaderboardController;

    @Test
    void getChallengeLeaderboard_shouldReturnLeaderboard() {
        Long challengeId = 1L;

        List<LeaderboardEntryDTO> leaderboard = List.of(
                mock(LeaderboardEntryDTO.class),
                mock(LeaderboardEntryDTO.class)
        );

        when(leaderboardService.getChallengeLeaderboard(challengeId))
                .thenReturn(leaderboard);

        ResponseEntity<List<LeaderboardEntryDTO>> response =
                leaderboardController.getChallengeLeaderboard(challengeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(leaderboard, response.getBody());

        verify(leaderboardService).getChallengeLeaderboard(challengeId);
    }

    @Test
    void getChallengeLeaderboard_shouldReturnEmptyList() {
        Long challengeId = 1L;

        when(leaderboardService.getChallengeLeaderboard(challengeId))
                .thenReturn(List.of());

        ResponseEntity<List<LeaderboardEntryDTO>> response =
                leaderboardController.getChallengeLeaderboard(challengeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(leaderboardService).getChallengeLeaderboard(challengeId);
    }

    @Test
    void getGlobalLeaderboard_shouldReturnLeaderboard() {
        List<GlobalLeaderboardEntryDTO> leaderboard = List.of(
                mock(GlobalLeaderboardEntryDTO.class),
                mock(GlobalLeaderboardEntryDTO.class)
        );

        when(leaderboardService.getGlobalLeaderboard())
                .thenReturn(leaderboard);

        ResponseEntity<List<GlobalLeaderboardEntryDTO>> response =
                leaderboardController.getGlobalLeaderboard();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(leaderboard, response.getBody());

        verify(leaderboardService).getGlobalLeaderboard();
    }

    @Test
    void getGlobalLeaderboard_shouldReturnEmptyList() {
        when(leaderboardService.getGlobalLeaderboard())
                .thenReturn(List.of());

        ResponseEntity<List<GlobalLeaderboardEntryDTO>> response =
                leaderboardController.getGlobalLeaderboard();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(leaderboardService).getGlobalLeaderboard();
    }
}
