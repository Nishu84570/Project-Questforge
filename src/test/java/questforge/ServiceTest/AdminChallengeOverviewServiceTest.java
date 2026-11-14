
package questforge.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import questforge.dto.AdminChallengeOverviewDTO;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.repository.ChallengeRepository;
import questforge.repository.SubmissionRepository;
import questforge.service.AdminChallengeOverviewService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminChallengeOverviewServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private AdminChallengeOverviewService service;

    private Challenge challenge;

    @BeforeEach
    void setUp() {

        challenge = new Challenge();

        challenge.setId(1L);
        challenge.setTitle("Two Sum");
        challenge.setDescription("Find two numbers whose sum equals target");
        challenge.setDifficulty("Easy");
        challenge.setCategory("Arrays");
    }


    private Submission createSubmission(
            Long challengeId,
            Long userId,
            Long score,
            Boolean firstSubmission) {

        Submission submission = new Submission();

        submission.setChallengeId(challengeId);
        submission.setUserId(userId);
        submission.setScore(score);
        submission.setFirstSubmission(firstSubmission);

        return submission;
    }

    @Test
    void getChallengeOverview_ShouldReturnCorrectStatistics() {

        List<Submission> submissions = List.of(
                createSubmission(1L, 101L, 100L, true),
                createSubmission(1L, 102L, 80L, true),
                createSubmission(1L, 101L, 90L, false),
                createSubmission(1L, 103L, 60L, false)
        );

        when(challengeRepository.findAll())
                .thenReturn(List.of(challenge));

        when(submissionRepository.findAll())
                .thenReturn(submissions);

        List<AdminChallengeOverviewDTO> result =
                service.getChallengeOverview();

        assertNotNull(result);
        assertEquals(1, result.size());

        AdminChallengeOverviewDTO dto = result.get(0);

        assertEquals(1L, dto.getChallengeId());
        assertEquals("Two Sum", dto.getTitle());
        assertEquals("Easy", dto.getDifficulty());
        assertEquals("Arrays", dto.getCategory());

        assertEquals(4L, dto.getTotalSubmissions());
        assertEquals(2L, dto.getFirstSubmissions());
        assertEquals(3L, dto.getUniqueParticipants());

        assertEquals(82.5, dto.getAverageScore(), 0.001);
        assertEquals(100L, dto.getHighestScore());
        assertEquals(60L, dto.getLowestScore());

        assertEquals(33.333, dto.getCompletionRate(), 0.01);

        verify(challengeRepository, times(1)).findAll();
        verify(submissionRepository, times(1)).findAll();
    }

  
    @Test
    void getChallengeOverview_WhenNoChallenges_ShouldReturnEmptyList() {

        when(challengeRepository.findAll())
                .thenReturn(List.of());

        when(submissionRepository.findAll())
                .thenReturn(List.of());

        List<AdminChallengeOverviewDTO> result =
                service.getChallengeOverview();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(challengeRepository).findAll();
        verify(submissionRepository).findAll();
    }

    @Test
    void getChallengeOverview_WhenNoSubmissions_ShouldReturnZeroStatistics() {

        when(challengeRepository.findAll())
                .thenReturn(List.of(challenge));

        when(submissionRepository.findAll())
                .thenReturn(List.of());

        List<AdminChallengeOverviewDTO> result =
                service.getChallengeOverview();

        assertEquals(1, result.size());

        AdminChallengeOverviewDTO dto = result.get(0);

        assertEquals(0L, dto.getTotalSubmissions());
        assertEquals(0L, dto.getFirstSubmissions());
        assertEquals(0L, dto.getUniqueParticipants());

        assertEquals(0.0, dto.getAverageScore(), 0.001);

        assertNull(dto.getHighestScore());
        assertNull(dto.getLowestScore());

        assertEquals(0.0, dto.getCompletionRate(), 0.001);
    }

    @Test
    void getChallengeOverview_ShouldIgnoreOtherChallengeSubmissions() {

        List<Submission> submissions = List.of(
                createSubmission(1L, 101L, 90L, true),
                createSubmission(2L, 102L, 100L, true)
        );

        when(challengeRepository.findAll())
                .thenReturn(List.of(challenge));

        when(submissionRepository.findAll())
                .thenReturn(submissions);

        List<AdminChallengeOverviewDTO> result =
                service.getChallengeOverview();

        AdminChallengeOverviewDTO dto = result.get(0);

        assertEquals(1L, dto.getTotalSubmissions());
        assertEquals(1L, dto.getFirstSubmissions());
        assertEquals(1L, dto.getUniqueParticipants());

        assertEquals(90.0, dto.getAverageScore(), 0.001);
        assertEquals(90L, dto.getHighestScore());
        assertEquals(90L, dto.getLowestScore());

        assertEquals(0.0, dto.getCompletionRate(), 0.001);
    }

    @Test
    void getChallengeOverview_ShouldCountOnlyTrueFirstSubmissions() {

        List<Submission> submissions = List.of(
                createSubmission(1L, 101L, 100L, true),
                createSubmission(1L, 102L, 100L, false),
                createSubmission(1L, 103L, 90L, null)
        );

        when(challengeRepository.findAll())
                .thenReturn(List.of(challenge));

        when(submissionRepository.findAll())
                .thenReturn(submissions);

        List<AdminChallengeOverviewDTO> result =
                service.getChallengeOverview();

        AdminChallengeOverviewDTO dto = result.get(0);

        assertEquals(3L, dto.getTotalSubmissions());
        assertEquals(1L, dto.getFirstSubmissions());
        assertEquals(3L, dto.getUniqueParticipants());

        assertEquals(33.333, dto.getCompletionRate(), 0.01);
    }

 
    @Test
    void getChallengeOverview_ShouldIgnoreNullUserIds() {

        List<Submission> submissions = List.of(
                createSubmission(1L, 101L, 100L, true),
                createSubmission(1L, null, 100L, true),
                createSubmission(1L, 102L, 70L, false)
        );

        when(challengeRepository.findAll())
                .thenReturn(List.of(challenge));

        when(submissionRepository.findAll())
                .thenReturn(submissions);

        List<AdminChallengeOverviewDTO> result =
                service.getChallengeOverview();

        AdminChallengeOverviewDTO dto = result.get(0);

        assertEquals(2L, dto.getUniqueParticipants());
        assertEquals(2L, dto.getFirstSubmissions());

        assertEquals(50.0, dto.getCompletionRate(), 0.001);
    }

  
    @Test
    void getChallengeOverview_ShouldCountUniquePerfectFirstAttemptUsers() {

        List<Submission> submissions = List.of(
                createSubmission(1L, 101L, 100L, true),
                createSubmission(1L, 101L, 120L, true),
                createSubmission(1L, 102L, 80L, true)
        );

        when(challengeRepository.findAll())
                .thenReturn(List.of(challenge));

        when(submissionRepository.findAll())
                .thenReturn(submissions);

        List<AdminChallengeOverviewDTO> result =
                service.getChallengeOverview();

        AdminChallengeOverviewDTO dto = result.get(0);

        assertEquals(3L, dto.getTotalSubmissions());
        assertEquals(3L, dto.getFirstSubmissions());
        assertEquals(2L, dto.getUniqueParticipants());

        assertEquals(120L, dto.getHighestScore(), 0.001);
        assertEquals(80L, dto.getLowestScore(), 0.001);

        assertEquals(50.0, dto.getCompletionRate(), 0.001);
    }

   
    @Test
    void getChallengeOverview_ShouldCalculateStatisticsForMultipleChallenges() {

        Challenge secondChallenge = new Challenge();

        secondChallenge.setId(2L);
        secondChallenge.setTitle("Binary Search");
        secondChallenge.setDescription("Search an element efficiently");
        secondChallenge.setDifficulty("Medium");
        secondChallenge.setCategory("Searching");

        List<Submission> submissions = List.of(
                createSubmission(1L, 101L, 100L, true),
                createSubmission(2L, 102L, 80L, true),
                createSubmission(2L, 103L, 60L, false)
        );

        when(challengeRepository.findAll())
                .thenReturn(List.of(challenge, secondChallenge));

        when(submissionRepository.findAll())
                .thenReturn(submissions);

        List<AdminChallengeOverviewDTO> result =
                service.getChallengeOverview();

        assertEquals(2, result.size());

        AdminChallengeOverviewDTO first = result.get(0);
        AdminChallengeOverviewDTO second = result.get(1);

        assertEquals(1L, first.getChallengeId());
        assertEquals(1L, first.getTotalSubmissions());
        assertEquals(100.0, first.getAverageScore(), 0.001);

        assertEquals(2L, second.getChallengeId());
        assertEquals(2L, second.getTotalSubmissions());
        assertEquals(70.0, second.getAverageScore(), 0.001);

        verify(challengeRepository, times(1)).findAll();
        verify(submissionRepository, times(1)).findAll();
    }
}
