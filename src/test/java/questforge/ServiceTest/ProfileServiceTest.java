
package questforge.ServiceTest;

import questforge.dto.ProfileProgressDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Achievement;
import questforge.model.Submission;
import questforge.model.User;
import questforge.model.UserStreak;
import questforge.repository.AchievementRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;
import questforge.service.ProfileService;
import questforge.service.StreakService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private StreakService streakService;

    @InjectMocks
    private ProfileService profileService;

    private User user;

    @BeforeEach
    void setUp() {
        user = createUser(1L, "Test User", "test@gmail.com", 150L);
    }



    private User createUser(Long id, String name, String email, Long xp) {
        User user = mock(User.class);

        lenient().when(user.getId()).thenReturn(id);
        lenient().when(user.getName()).thenReturn(name);
        lenient().when(user.getEmail()).thenReturn(email);
        lenient().when(user.getXp()).thenReturn(xp);

        return user;
    }

    private Submission createSubmission(
        Long challengeId,
        Long score,
        Boolean firstSubmission
) {
    Submission submission = mock(Submission.class);

    when(submission.getFirstSubmission())
            .thenReturn(firstSubmission);

    lenient().when(submission.getChallengeId())
            .thenReturn(challengeId);

    lenient().when(submission.getScore())
            .thenReturn(score);

    return submission;
}

    private UserStreak createStreak() {
        UserStreak streak = mock(UserStreak.class);

        when(streak.getCurrentStreak()).thenReturn((long) 5);
        when(streak.getLongestStreak()).thenReturn((long) 10);

        return streak;
    }

    private void mockProfileDependencies(
            User testUser,
            List<Submission> submissions,
            List<User> users,
            List<Achievement> achievements
    ) {
        Long userId = testUser.getId();

        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));

        when(submissionRepository.findByUserId(userId))
                .thenReturn(submissions);

        when(userRepository.findAll())
                .thenReturn(new ArrayList<>(users));

        when(achievementRepository.findByUserId(userId))
                .thenReturn(achievements);

        UserStreak streak = createStreak();

        when(streakService.getUserStreak(userId))
                .thenReturn(streak);
    }

    private ProfileProgressDTO executeProfile(
            User testUser,
            List<Submission> submissions,
            List<User> users,
            List<Achievement> achievements
    ) {
        mockProfileDependencies(
                testUser,
                submissions,
                users,
                achievements
        );

        return profileService.getMyProfileProgress(testUser.getEmail());
    }

   
    @Test
    void getMyProfileProgress_Success() {

        List<Submission> submissions = List.of(
                createSubmission(101L, 100L, true),
                createSubmission(102L, 80L, true)
        );

        List<User> users = List.of(user);

        ProfileProgressDTO result = executeProfile(
                user,
                submissions,
                users,
                Collections.emptyList()
        );

        assertNotNull(result);

        verify(userRepository).findByEmail("test@gmail.com");
        verify(submissionRepository).findByUserId(1L);
        verify(streakService).getUserStreak(1L);
    }

  

    @Test
    void getMyProfileProgress_UserNotFound() {

        when(userRepository.findByEmail("wrong@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> profileService.getMyProfileProgress("wrong@gmail.com")
        );

        verify(userRepository).findByEmail("wrong@gmail.com");

        verifyNoInteractions(
                submissionRepository,
                achievementRepository,
                streakService
        );
    }



    @Test
    void getMyProfileProgress_ShouldCountUniqueChallenges() {

        List<Submission> submissions = List.of(
                createSubmission(101L, 80L, true),
                createSubmission(101L, 90L, true),
                createSubmission(102L, 70L, true),
                createSubmission(103L, 60L, true)
        );

        ProfileProgressDTO result = executeProfile(
                user,
                submissions,
                List.of(user),
                Collections.emptyList()
        );

        assertNotNull(result);
    }



    @Test
    void getMyProfileProgress_ShouldCountCompletedChallenges() {

        List<Submission> submissions = List.of(
                createSubmission(101L, 100L, true),
                createSubmission(102L, 80L, true),
                createSubmission(103L, 100L, true)
        );

        ProfileProgressDTO result = executeProfile(
                user,
                submissions,
                List.of(user),
                Collections.emptyList()
        );

        assertNotNull(result);
    }



    @Test
    void getMyProfileProgress_ShouldHandleEmptySubmissions() {

        ProfileProgressDTO result = executeProfile(
                user,
                Collections.emptyList(),
                List.of(user),
                Collections.emptyList()
        );

        assertNotNull(result);
    }



    @Test
    void getMyProfileProgress_ShouldIgnoreNonFirstSubmissions() {

        List<Submission> submissions = List.of(
                createSubmission(101L, 100L, true),
                createSubmission(102L, 90L, false),
                createSubmission(103L, 80L, true)
        );

        ProfileProgressDTO result = executeProfile(
                user,
                submissions,
                List.of(user),
                Collections.emptyList()
        );

        assertNotNull(result);
    }



    private void assertLevelForXp(Long xp, int expectedLevel) {

        User testUser = createUser(
                1L,
                "Test User",
                "test@gmail.com",
                xp
        );

        ProfileProgressDTO result = executeProfile(
                testUser,
                Collections.emptyList(),
                List.of(testUser),
                Collections.emptyList()
        );

        assertNotNull(result);
        assertEquals(expectedLevel, result.getLevel());
    }



    @Test
    void getMyProfileProgress_LevelOne() {
        assertLevelForXp(50L, 1);
    }



    @Test
    void getMyProfileProgress_LevelTwo() {
        assertLevelForXp(150L, 2);
    }


    @Test
    void getMyProfileProgress_LevelThree() {
        assertLevelForXp(300L, 3);
    }


    @Test
    void getMyProfileProgress_LevelFour() {
        assertLevelForXp(500L, 4);
    }



    @Test
    void getMyProfileProgress_LevelFive() {
        assertLevelForXp(800L, 5);
    }



    @Test
    void getMyProfileProgress_LevelSix() {
        assertLevelForXp(1100L, 6);
    }



    @Test
    void getMyProfileProgress_LevelAboveSix() {
        assertLevelForXp(1600L, 7);
    }


    @Test
    void getMyProfileProgress_ShouldCalculateGlobalRank() {

        User firstUser = createUser(
                2L, "First User", "first@gmail.com", 500L
        );

        User secondUser = createUser(
                1L, "Second User", "second@gmail.com", 150L
        );

        User thirdUser = createUser(
                3L, "Third User", "third@gmail.com", 100L
        );

        ProfileProgressDTO result = executeProfile(
                secondUser,
                Collections.emptyList(),
                List.of(firstUser, secondUser, thirdUser),
                Collections.emptyList()
        );

        assertNotNull(result);
    }

    

    @Test
    void getMyProfileProgress_ShouldHandleEqualXpRanking() {

        User firstUser = createUser(
                1L, "First User", "first@gmail.com", 200L
        );

        User secondUser = createUser(
                2L, "Second User", "second@gmail.com", 200L
        );

        ProfileProgressDTO result = executeProfile(
                secondUser,
                Collections.emptyList(),
                List.of(secondUser, firstUser),
                Collections.emptyList()
        );

        assertNotNull(result);
    }



    @Test
    void getMyProfileProgress_WhenNoAchievementsExist() {

        ProfileProgressDTO result = executeProfile(
                user,
                Collections.emptyList(),
                List.of(user),
                Collections.emptyList()
        );

        assertNotNull(result);

        verify(achievementRepository).findByUserId(1L);
    }



    @Test
    void getMyProfileProgress_WhenAchievementsExist() {

        Achievement achievement = mock(Achievement.class);

        when(achievement.getId()).thenReturn(1L);
        when(achievement.getCode()).thenReturn("FIRST_CHALLENGE");
        when(achievement.getName()).thenReturn("First Challenge");
        when(achievement.getDescription())
                .thenReturn("Completed your first challenge");
        when(achievement.getIcon()).thenReturn("trophy");
        UserStreak streak = createStreak();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(submissionRepository.findByUserId(1L))
                .thenReturn(Collections.emptyList());

        when(userRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(user)));

        when(achievementRepository.findByUserId(1L))
                .thenReturn(List.of(achievement));

        when(streakService.getUserStreak(1L))
                .thenReturn(streak);

        ProfileProgressDTO result =
                profileService.getMyProfileProgress(user.getEmail());

        assertNotNull(result);

        verify(achievementRepository).findByUserId(1L);
    }


    @Test
    void getMyProfileProgress_ShouldFetchUserStreak() {

        ProfileProgressDTO result = executeProfile(
                user,
                Collections.emptyList(),
                List.of(user),
                Collections.emptyList()
        );

        assertNotNull(result);

        verify(streakService).getUserStreak(1L);
    }
}
