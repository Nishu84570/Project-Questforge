
package questforge.ServiceTest;

import questforge.service.LeaderboardService;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;
import questforge.repository.ChallengeRepository;
import questforge.model.Submission;
import questforge.model.User;
import questforge.model.Challenge;
import questforge.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    private Challenge challenge;

    @BeforeEach
    void setUp() {
        challenge = mock(Challenge.class);

        lenient().when(challenge.getId()).thenReturn(1L);
        lenient().when(challenge.getTitle()).thenReturn("Java Challenge");
    }

   

    private Submission createSubmission(
            Long userId,
            long score,
            long correctAnswers,
            long totalQuestions,
            LocalDateTime submittedAt) {

        Submission submission = mock(Submission.class);

        when(submission.getUserId()).thenReturn(userId);
        when(submission.getScore()).thenReturn(score);
        when(submission.getCorrectAnswers()).thenReturn(correctAnswers);
        when(submission.getTotalQuestions()).thenReturn(totalQuestions);
        when(submission.getSubmittedAt()).thenReturn(submittedAt);

        return submission;
    }

    private User createUser(Long id, String name, long xp) {

        User user = mock(User.class);

        when(user.getId()).thenReturn(id);
        when(user.getName()).thenReturn(name);
        lenient().when(user.getXp()).thenReturn(xp);

        return user;
    }

    // --------------------------------------------------
    // Challenge Leaderboard Tests
    // --------------------------------------------------

    @Test
    void getChallengeLeaderboard_shouldReturnLeaderboardSuccessfully() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        Submission submission1 = createSubmission(
                101L, 90, 8, 10, LocalDateTime.now());

        when(submissionRepository.findByChallengeIdAndFirstSubmissionTrue(1L))
                .thenReturn(List.of(submission1));

        User user = createUser(101L, "Rahul", 500);

        when(userRepository.findAllById(List.of(101L)))
                .thenReturn(List.of(user));

        var result = leaderboardService.getChallengeLeaderboard(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals("Rahul", result.get(0).getUserName());

        verify(challengeRepository).findById(1L);
        verify(submissionRepository)
                .findByChallengeIdAndFirstSubmissionTrue(1L);
        verify(userRepository).findAllById(List.of(101L));
    }

    @Test
    void getChallengeLeaderboard_shouldThrowExceptionWhenChallengeNotFound() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> leaderboardService.getChallengeLeaderboard(1L)
        );

        verify(challengeRepository).findById(1L);

        verifyNoInteractions(submissionRepository, userRepository);
    }

    @Test
    void getChallengeLeaderboard_shouldReturnEmptyListWhenNoSubmissions() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        when(submissionRepository.findByChallengeIdAndFirstSubmissionTrue(1L))
                .thenReturn(List.of());

        var result = leaderboardService.getChallengeLeaderboard(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Service calls findAllById with an empty list.
        verify(userRepository).findAllById(List.of());
    }

    @Test
    void getChallengeLeaderboard_shouldSortByScoreDescending() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        Submission s1 = createSubmission(
                101L, 50, 5, 10, LocalDateTime.now());

        Submission s2 = createSubmission(
                102L, 100, 9, 10, LocalDateTime.now());

        Submission s3 = createSubmission(
                103L, 75, 7, 10, LocalDateTime.now());

        when(submissionRepository.findByChallengeIdAndFirstSubmissionTrue(1L))
                .thenReturn(List.of(s1, s2, s3));

        User user1 = createUser(101L, "Aman", 100);
        User user2 = createUser(102L, "Rahul", 200);
        User user3 = createUser(103L, "Priya", 300);

        when(userRepository.findAllById(List.of(102L, 103L, 101L)))
                .thenReturn(List.of(user1, user2, user3));

        var result = leaderboardService.getChallengeLeaderboard(1L);

        assertEquals(3, result.size());
        assertEquals(102L, result.get(0).getUserId());
        assertEquals(103L, result.get(1).getUserId());
        assertEquals(101L, result.get(2).getUserId());
    }

    @Test
    void getChallengeLeaderboard_shouldApplyTieBreakingRules() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        Submission s1 = createSubmission(
                101L, 100, 7, 10,
                LocalDateTime.of(2026, 1, 1, 10, 0));

        Submission s2 = createSubmission(
                102L, 100, 8, 10,
                LocalDateTime.of(2026, 1, 1, 9, 0));

        Submission s3 = createSubmission(
                103L, 100, 8, 10,
                LocalDateTime.of(2026, 1, 1, 11, 0));

        when(submissionRepository.findByChallengeIdAndFirstSubmissionTrue(1L))
                .thenReturn(List.of(s1, s2, s3));

        User user1 = createUser(101L, "Aman", 100);
        User user2 = createUser(102L, "Rahul", 200);
        User user3 = createUser(103L, "Priya", 300);

        when(userRepository.findAllById(List.of(102L, 103L, 101L)))
                .thenReturn(List.of(user1, user2, user3));

        var result = leaderboardService.getChallengeLeaderboard(1L);

        assertEquals(102L, result.get(0).getUserId());
        assertEquals(103L, result.get(1).getUserId());
        assertEquals(101L, result.get(2).getUserId());
    }

    @Test
    void getChallengeLeaderboard_shouldSortBySubmissionIdWhenOtherFieldsMatch() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        Submission s1 = mock(Submission.class);
        Submission s2 = mock(Submission.class);

        when(s1.getId()).thenReturn(20L);
        when(s2.getId()).thenReturn(10L);

        when(s1.getUserId()).thenReturn(102L);
        when(s2.getUserId()).thenReturn(101L);

        when(s1.getScore()).thenReturn(100L);
        when(s2.getScore()).thenReturn(100L);

        when(s1.getCorrectAnswers()).thenReturn(8L);
        when(s2.getCorrectAnswers()).thenReturn(8L);

        when(s1.getSubmittedAt()).thenReturn(
                LocalDateTime.of(2026, 1, 1, 10, 0));

        when(s2.getSubmittedAt()).thenReturn(
                LocalDateTime.of(2026, 1, 1, 10, 0));

        when(submissionRepository.findByChallengeIdAndFirstSubmissionTrue(1L))
                .thenReturn(List.of(s1, s2));

        User user1 = createUser(101L, "Aman", 100);
        User user2 = createUser(102L, "Rahul", 200);

        when(userRepository.findAllById(List.of(101L, 102L)))
                .thenReturn(List.of(user1, user2));

        var result = leaderboardService.getChallengeLeaderboard(1L);

        assertEquals(101L, result.get(0).getUserId());
        assertEquals(102L, result.get(1).getUserId());
    }

    @Test
    void getChallengeLeaderboard_shouldPlaceNullSubmissionTimeLast() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        Submission s1 = createSubmission(
                101L, 100, 8, 10, null);

        Submission s2 = createSubmission(
                102L, 100, 8, 10,
                LocalDateTime.of(2026, 1, 1, 10, 0));

        when(submissionRepository.findByChallengeIdAndFirstSubmissionTrue(1L))
                .thenReturn(List.of(s1, s2));

        User user1 = createUser(101L, "Aman", 100);
        User user2 = createUser(102L, "Rahul", 200);

        when(userRepository.findAllById(List.of(102L, 101L)))
                .thenReturn(List.of(user1, user2));

        var result = leaderboardService.getChallengeLeaderboard(1L);

        assertEquals(102L, result.get(0).getUserId());
        assertEquals(101L, result.get(1).getUserId());
    }

    @Test
    void getChallengeLeaderboard_shouldShowUnknownUserWhenUserNotFound() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        Submission submission = createSubmission(
                101L, 90, 8, 10, LocalDateTime.now());

        when(submissionRepository.findByChallengeIdAndFirstSubmissionTrue(1L))
                .thenReturn(List.of(submission));

        when(userRepository.findAllById(List.of(101L)))
                .thenReturn(List.of());

        var result = leaderboardService.getChallengeLeaderboard(1L);

        assertEquals("Unknown User", result.get(0).getUserName());
    }

    @Test
    void getChallengeLeaderboard_shouldExcludeNullUserIdsFromLookup() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        Submission submission = createSubmission(
                null, 90, 8, 10, LocalDateTime.now());

        when(submissionRepository.findByChallengeIdAndFirstSubmissionTrue(1L))
                .thenReturn(List.of(submission));

        var result = leaderboardService.getChallengeLeaderboard(1L);

        assertNotNull(result);

        // Null user IDs are excluded, so the resulting lookup list is empty.
        verify(userRepository).findAllById(List.of());
    }

    // --------------------------------------------------
    // Global Leaderboard Tests
    // --------------------------------------------------

    @Test
    void getGlobalLeaderboard_shouldSortUsersByXpDescending() {

        User user1 = createUser(1L, "Rahul", 100);
        User user2 = createUser(2L, "Aman", 500);
        User user3 = createUser(3L, "Priya", 300);

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2, user3));

        var result = leaderboardService.getGlobalLeaderboard();

        assertEquals(3, result.size());
        assertEquals(2L, result.get(0).getUserId());
        assertEquals(3L, result.get(1).getUserId());
        assertEquals(1L, result.get(2).getUserId());
    }

    @Test
    void getGlobalLeaderboard_shouldSortEqualXpByUserIdAscending() {

        User user1 = createUser(3L, "Priya", 500);
        User user2 = createUser(1L, "Rahul", 500);
        User user3 = createUser(2L, "Aman", 500);

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2, user3));

        var result = leaderboardService.getGlobalLeaderboard();

        assertEquals(1L, result.get(0).getUserId());
        assertEquals(2L, result.get(1).getUserId());
        assertEquals(3L, result.get(2).getUserId());
    }

    @Test
    void getGlobalLeaderboard_shouldReturnEmptyListWhenNoUsers() {

        when(userRepository.findAll()).thenReturn(List.of());

        var result = leaderboardService.getGlobalLeaderboard();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getGlobalLeaderboard_shouldCalculateCorrectLevels() {

        List<User> users = List.of(
                createUser(1L, "Level1", 50),
                createUser(2L, "Level2", 100),
                createUser(3L, "Level3", 250),
                createUser(4L, "Level4", 450),
                createUser(5L, "Level5", 700),
                createUser(6L, "Level6", 1000),
                createUser(7L, "Level7", 1500)
        );

        when(userRepository.findAll()).thenReturn(users);

        var result = leaderboardService.getGlobalLeaderboard();

        assertEquals(1, result.stream()
                .filter(e -> e.getUserId().equals(1L))
                .findFirst().orElseThrow().getLevel());

        assertEquals(2, result.stream()
                .filter(e -> e.getUserId().equals(2L))
                .findFirst().orElseThrow().getLevel());

        assertEquals(3, result.stream()
                .filter(e -> e.getUserId().equals(3L))
                .findFirst().orElseThrow().getLevel());

        assertEquals(4, result.stream()
                .filter(e -> e.getUserId().equals(4L))
                .findFirst().orElseThrow().getLevel());

        assertEquals(5, result.stream()
                .filter(e -> e.getUserId().equals(5L))
                .findFirst().orElseThrow().getLevel());

        assertEquals(6, result.stream()
                .filter(e -> e.getUserId().equals(6L))
                .findFirst().orElseThrow().getLevel());

        assertEquals(7, result.stream()
                .filter(e -> e.getUserId().equals(7L))
                .findFirst().orElseThrow().getLevel());
    }
}
