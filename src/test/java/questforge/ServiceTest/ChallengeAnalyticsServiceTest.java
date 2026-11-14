
package questforge.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import questforge.dto.ChallengeAnalyticsResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.repository.ChallengeRepository;
import questforge.repository.SubmissionRepository;
import questforge.service.ChallengeAnalyticsService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeAnalyticsServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private ChallengeAnalyticsService challengeAnalyticsService;

    private Challenge challenge;

    @BeforeEach
    void setUp() {

        challenge = new Challenge();
        challenge.setId(1L);
        challenge.setTitle("Java Challenge");
    }


    @Test
    void getChallengeAnalytics_Success() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        Submission s1 = createSubmission(80, true, "COMPLETED");
        Submission s2 = createSubmission(60, true, "APPROVED");
        Submission s3 = createSubmission(90, true, "PENDING");
        Submission s4 = createSubmission(100, false, "COMPLETED");

        when(submissionRepository.findByChallengeId(1L))
                .thenReturn(List.of(s1, s2, s3, s4));

        ChallengeAnalyticsResponseDTO result =
                challengeAnalyticsService.getChallengeAnalytics(1L);

        assertNotNull(result);

        assertEquals(1L, result.getChallengeId());
        assertEquals("Java Challenge", result.getTitle());

        assertEquals(4L, result.getTotalSubmissions());
        assertEquals(3L, result.getFirstSubmissions());

        assertEquals(230.0 / 3, result.getAverageScore(), 0.001);

        assertEquals(200.0 / 3, result.getCompletionRate(), 0.001);

        verify(challengeRepository, times(1)).findById(1L);
        verify(submissionRepository, times(1)).findByChallengeId(1L);
    }

    @Test
    void getChallengeAnalytics_ChallengeNotFound() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> challengeAnalyticsService.getChallengeAnalytics(1L)
        );

        verify(challengeRepository, times(1)).findById(1L);

        verifyNoInteractions(submissionRepository);
    }

    @Test
    void getChallengeAnalytics_NoSubmissions() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        when(submissionRepository.findByChallengeId(1L))
                .thenReturn(List.of());

        ChallengeAnalyticsResponseDTO result =
                challengeAnalyticsService.getChallengeAnalytics(1L);

        assertNotNull(result);

        assertEquals(0L, result.getTotalSubmissions());
        assertEquals(0L, result.getFirstSubmissions());
        assertEquals(0.0, result.getAverageScore());
        assertEquals(0.0, result.getCompletionRate());
    }

    @Test
    void getChallengeAnalytics_NoFirstSubmissions() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        Submission s1 = createSubmission(80, false, "COMPLETED");
        Submission s2 = createSubmission(90, false, "APPROVED");

        when(submissionRepository.findByChallengeId(1L))
                .thenReturn(List.of(s1, s2));

        ChallengeAnalyticsResponseDTO result =
                challengeAnalyticsService.getChallengeAnalytics(1L);

        assertEquals(2L, result.getTotalSubmissions());
        assertEquals(0L, result.getFirstSubmissions());
        assertEquals(0.0, result.getAverageScore());
        assertEquals(0.0, result.getCompletionRate());
    }

    @Test
    void getChallengeAnalytics_CaseInsensitiveStatus() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        Submission s1 = createSubmission(80, true, "completed");
        Submission s2 = createSubmission(90, true, "approved");
        Submission s3 = createSubmission(70, true, "pending");

        when(submissionRepository.findByChallengeId(1L))
                .thenReturn(List.of(s1, s2, s3));

        ChallengeAnalyticsResponseDTO result =
                challengeAnalyticsService.getChallengeAnalytics(1L);

        assertEquals(3L, result.getFirstSubmissions());
        assertEquals(200.0 / 3, result.getCompletionRate(), 0.001);
    }

    private Submission createSubmission(
            long score,
            boolean firstSubmission,
            String status) {

        Submission submission = new Submission();

        submission.setScore(score);
        submission.setFirstSubmission(firstSubmission);
        submission.setStatus(status);

        return submission;
    }
}
