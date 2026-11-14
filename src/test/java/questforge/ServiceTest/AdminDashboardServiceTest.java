
package questforge.ServiceTest;

import questforge.dto.AdminDashboardResponseDTO;
import questforge.model.User;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.repository.UserRepository;
import questforge.repository.ChallengeRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.AchievementRepository;
import questforge.repository.NotificationRepository;
import questforge.service.AdminDashboardService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    private User createUser(Long id, String name, long xp) {

        User user = mock(User.class);

        when(user.getId()).thenReturn(id);
        when(user.getName()).thenReturn(name);
        when(user.getXp()).thenReturn(xp);

        return user;
    }

    
    private User createUserWithXp(long xp) {

        User user = mock(User.class);

        when(user.getXp()).thenReturn(xp);

        return user;
    }

    private Submission createSubmission(
            Long userId, Long challengeId, long score) {

        Submission submission = new Submission();

        submission.setUserId(userId);
        submission.setChallengeId(challengeId);
        submission.setScore(score);

        return submission;
    }


    private Challenge createChallenge(Long id, String title) {

        Challenge challenge = new Challenge();

        challenge.setId(id);
        challenge.setTitle(title);

        return challenge;
    }


    @Test
    void shouldReturnCorrectDashboardStats() {

        User user1 = createUserWithXp(100L);
        User user2 = createUser(2L, "Aman", 200L);

        List<User> users = List.of(user1, user2);

        List<Submission> submissions = List.of(
                createSubmission(1L, 10L, 80L),
                createSubmission(2L, 10L, 100L),
                createSubmission(1L, 20L, 60L)
        );

        List<Challenge> challenges = List.of(
                createChallenge(10L, "Java Basics"),
                createChallenge(20L, "Spring Boot")
        );

        when(userRepository.findAll()).thenReturn(users);
        when(submissionRepository.findAll()).thenReturn(submissions);
        when(challengeRepository.findAll()).thenReturn(challenges);

        when(achievementRepository.count()).thenReturn(5L);
        when(notificationRepository.count()).thenReturn(8L);

        AdminDashboardResponseDTO response =
                adminDashboardService.getDashboardStats();

        assertNotNull(response);

        assertEquals(2L, response.getTotalUsers());
        assertEquals(2L, response.getTotalChallenges());
        assertEquals(3L, response.getTotalSubmissions());

        assertEquals(5L, response.getTotalAchievements());
        assertEquals(8L, response.getTotalNotifications());

        assertEquals(300L, response.getTotalXp());

        assertEquals(150.0, response.getAverageUserXp());
        assertEquals(80.0, response.getAverageSubmissionScore());

        assertNotNull(response.getTopUser());
        assertEquals(2L, response.getTopUser().getUserId());
        assertEquals("Aman", response.getTopUser().getName());
        assertEquals(200L, response.getTopUser().getXp());

        assertNotNull(response.getMostActiveChallenge());

        assertEquals(
                10L,
                response.getMostActiveChallenge().getChallengeId()
        );

        assertEquals(
                "Java Basics",
                response.getMostActiveChallenge().getTitle()
        );

        assertEquals(
                2L,
                response.getMostActiveChallenge().getSubmissions()
        );
    }

    @Test
    void shouldReturnZeroStatsWhenDatabaseIsEmpty() {

        when(userRepository.findAll()).thenReturn(List.of());
        when(submissionRepository.findAll()).thenReturn(List.of());
        when(challengeRepository.findAll()).thenReturn(List.of());

        when(achievementRepository.count()).thenReturn(0L);
        when(notificationRepository.count()).thenReturn(0L);

        AdminDashboardResponseDTO response =
                adminDashboardService.getDashboardStats();

        assertNotNull(response);

        assertEquals(0L, response.getTotalUsers());
        assertEquals(0L, response.getTotalChallenges());
        assertEquals(0L, response.getTotalSubmissions());

        assertEquals(0L, response.getTotalAchievements());
        assertEquals(0L, response.getTotalNotifications());

        assertEquals(0L, response.getTotalXp());

        assertEquals(0.0, response.getAverageUserXp());
        assertEquals(0.0, response.getAverageSubmissionScore());

        assertNull(response.getTopUser());
        assertNull(response.getMostActiveChallenge());
    }


    @Test
    void shouldCalculateAverageUserXpCorrectly() {

        List<User> users = List.of(
                createUserWithXp(100L),
                createUserWithXp(200L),
                createUserWithXp(300L)
        );

        when(userRepository.findAll()).thenReturn(users);
        when(submissionRepository.findAll()).thenReturn(List.of());
        when(challengeRepository.findAll()).thenReturn(List.of());

        when(achievementRepository.count()).thenReturn(0L);
        when(notificationRepository.count()).thenReturn(0L);

        AdminDashboardResponseDTO response =
                adminDashboardService.getDashboardStats();

        assertEquals(600L, response.getTotalXp());
        assertEquals(200.0, response.getAverageUserXp());
    }

    @Test
    void shouldCalculateAverageSubmissionScoreCorrectly() {

        List<Submission> submissions = List.of(
                createSubmission(1L, 10L, 60L),
                createSubmission(2L, 10L, 80L),
                createSubmission(3L, 20L, 100L)
        );

        when(userRepository.findAll()).thenReturn(List.of());
        when(submissionRepository.findAll()).thenReturn(submissions);
        when(challengeRepository.findAll()).thenReturn(List.of());

        when(achievementRepository.count()).thenReturn(0L);
        when(notificationRepository.count()).thenReturn(0L);

        AdminDashboardResponseDTO response =
                adminDashboardService.getDashboardStats();

        assertEquals(80.0, response.getAverageSubmissionScore());
    }


    @Test
    void shouldReturnTopUserWithHighestXp() {

        List<User> users = List.of(
                createUserWithXp(100L),
                createUser(2L, "Aman", 500L),
                createUserWithXp(300L)
        );

        when(userRepository.findAll()).thenReturn(users);
        when(submissionRepository.findAll()).thenReturn(List.of());
        when(challengeRepository.findAll()).thenReturn(List.of());

        when(achievementRepository.count()).thenReturn(0L);
        when(notificationRepository.count()).thenReturn(0L);

        AdminDashboardResponseDTO response =
                adminDashboardService.getDashboardStats();

        assertNotNull(response.getTopUser());

        assertEquals(2L, response.getTopUser().getUserId());
        assertEquals("Aman", response.getTopUser().getName());
        assertEquals(500L, response.getTopUser().getXp());
    }

 
    @Test
    void shouldReturnMostActiveChallenge() {

        List<Submission> submissions = List.of(
                createSubmission(1L, 10L, 80L),
                createSubmission(2L, 10L, 90L),
                createSubmission(3L, 20L, 70L)
        );

        List<Challenge> challenges = List.of(
                createChallenge(10L, "Java Basics"),
                createChallenge(20L, "Spring Boot")
        );

        when(userRepository.findAll()).thenReturn(List.of());
        when(submissionRepository.findAll()).thenReturn(submissions);
        when(challengeRepository.findAll()).thenReturn(challenges);

        when(achievementRepository.count()).thenReturn(0L);
        when(notificationRepository.count()).thenReturn(0L);

        AdminDashboardResponseDTO response =
                adminDashboardService.getDashboardStats();

        assertNotNull(response.getMostActiveChallenge());

        assertEquals(
                10L,
                response.getMostActiveChallenge().getChallengeId()
        );

        assertEquals(
                "Java Basics",
                response.getMostActiveChallenge().getTitle()
        );

        assertEquals(
                2L,
                response.getMostActiveChallenge().getSubmissions()
        );
    }

  
    @Test
    void shouldIgnoreSubmissionsWithNullChallengeId() {

        List<Submission> submissions = List.of(
                createSubmission(1L, null, 80L),
                createSubmission(2L, 10L, 90L),
                createSubmission(3L, null, 70L)
        );

        List<Challenge> challenges = List.of(
                createChallenge(10L, "Java Basics")
        );

        when(userRepository.findAll()).thenReturn(List.of());
        when(submissionRepository.findAll()).thenReturn(submissions);
        when(challengeRepository.findAll()).thenReturn(challenges);

        when(achievementRepository.count()).thenReturn(0L);
        when(notificationRepository.count()).thenReturn(0L);

        AdminDashboardResponseDTO response =
                adminDashboardService.getDashboardStats();

        assertNotNull(response.getMostActiveChallenge());

        assertEquals(
                10L,
                response.getMostActiveChallenge().getChallengeId()
        );

        assertEquals(
                1L,
                response.getMostActiveChallenge().getSubmissions()
        );
    }

  
    @Test
    void shouldReturnDeletedChallengeWhenChallengeDoesNotExist() {

        List<Submission> submissions = List.of(
                createSubmission(1L, 99L, 80L),
                createSubmission(2L, 99L, 90L)
        );

        when(userRepository.findAll()).thenReturn(List.of());
        when(submissionRepository.findAll()).thenReturn(submissions);
        when(challengeRepository.findAll()).thenReturn(List.of());

        when(achievementRepository.count()).thenReturn(0L);
        when(notificationRepository.count()).thenReturn(0L);

        AdminDashboardResponseDTO response =
                adminDashboardService.getDashboardStats();

        assertNotNull(response.getMostActiveChallenge());

        assertEquals(
                99L,
                response.getMostActiveChallenge().getChallengeId()
        );

        assertEquals(
                "Deleted Challenge",
                response.getMostActiveChallenge().getTitle()
        );

        assertEquals(
                2L,
                response.getMostActiveChallenge().getSubmissions()
        );
    }

    @Test
    void shouldReturnNullMostActiveChallengeWhenNoSubmissionsExist() {

        when(userRepository.findAll()).thenReturn(List.of());
        when(submissionRepository.findAll()).thenReturn(List.of());
        when(challengeRepository.findAll()).thenReturn(List.of());

        when(achievementRepository.count()).thenReturn(0L);
        when(notificationRepository.count()).thenReturn(0L);

        AdminDashboardResponseDTO response =
                adminDashboardService.getDashboardStats();

        assertNull(response.getMostActiveChallenge());
        assertEquals(0.0, response.getAverageSubmissionScore());
    }


    @Test
    void shouldReturnNullTopUserWhenNoUsersExist() {

        when(userRepository.findAll()).thenReturn(List.of());
        when(submissionRepository.findAll()).thenReturn(List.of());
        when(challengeRepository.findAll()).thenReturn(List.of());

        when(achievementRepository.count()).thenReturn(0L);
        when(notificationRepository.count()).thenReturn(0L);

        AdminDashboardResponseDTO response =
                adminDashboardService.getDashboardStats();

        assertNull(response.getTopUser());
        assertEquals(0.0, response.getAverageUserXp());
        assertEquals(0L, response.getTotalXp());
    }


    @Test
    void shouldCallAllRepositories() {

        when(userRepository.findAll()).thenReturn(List.of());
        when(submissionRepository.findAll()).thenReturn(List.of());
        when(challengeRepository.findAll()).thenReturn(List.of());

        when(achievementRepository.count()).thenReturn(0L);
        when(notificationRepository.count()).thenReturn(0L);

        adminDashboardService.getDashboardStats();

        verify(userRepository, times(1)).findAll();
        verify(submissionRepository, times(1)).findAll();
        verify(challengeRepository, times(1)).findAll();

        verify(achievementRepository, times(1)).count();
        verify(notificationRepository, times(1)).count();
    }
}
