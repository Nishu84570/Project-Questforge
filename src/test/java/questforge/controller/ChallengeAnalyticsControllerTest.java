
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.ChallengeAnalyticsResponseDTO;
import questforge.service.ChallengeAnalyticsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeAnalyticsControllerTest {

    @Mock
    private ChallengeAnalyticsService challengeAnalyticsService;

    @Mock
    private ChallengeAnalyticsResponseDTO analyticsDTO;

    @InjectMocks
    private ChallengeAnalyticsController challengeAnalyticsController;

    private final Long CHALLENGE_ID = 101L;

 
    @Test
    void getChallengeAnalytics_ShouldReturnAnalytics_WhenChallengeExists() {

        when(challengeAnalyticsService.getChallengeAnalytics(CHALLENGE_ID))
                .thenReturn(analyticsDTO);

        ResponseEntity<ChallengeAnalyticsResponseDTO> response =
                challengeAnalyticsController.getChallengeAnalytics(CHALLENGE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(analyticsDTO, response.getBody());

        verify(challengeAnalyticsService, times(1))
                .getChallengeAnalytics(CHALLENGE_ID);
    }


    @Test
    void getChallengeAnalytics_ShouldPassCorrectChallengeId() {

        when(challengeAnalyticsService.getChallengeAnalytics(CHALLENGE_ID))
                .thenReturn(analyticsDTO);

        challengeAnalyticsController.getChallengeAnalytics(CHALLENGE_ID);

        verify(challengeAnalyticsService)
                .getChallengeAnalytics(CHALLENGE_ID);
    }


    @Test
    void getChallengeAnalytics_ShouldPropagateException_WhenServiceFails() {

        when(challengeAnalyticsService.getChallengeAnalytics(CHALLENGE_ID))
                .thenThrow(new RuntimeException("Analytics not found"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> challengeAnalyticsController
                        .getChallengeAnalytics(CHALLENGE_ID)
        );

        assertEquals("Analytics not found", exception.getMessage());

        verify(challengeAnalyticsService)
                .getChallengeAnalytics(CHALLENGE_ID);
    }

    @Test
    void getChallengeAnalytics_ShouldReturnOk_WhenServiceReturnsNull() {

        when(challengeAnalyticsService.getChallengeAnalytics(CHALLENGE_ID))
                .thenReturn(null);

        ResponseEntity<ChallengeAnalyticsResponseDTO> response =
                challengeAnalyticsController.getChallengeAnalytics(CHALLENGE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }
}
