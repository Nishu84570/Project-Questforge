
package questforge.ServiceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import questforge.dto.AdminUserResponseDTO;
import questforge.dto.AdminSubmissionResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.User;
import questforge.model.Submission;
import questforge.model.UserStreak;
import questforge.model.Challenge;
import questforge.repository.UserRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserStreakRepository;
import questforge.repository.ChallengeRepository;
import questforge.service.AdminUserService;
import questforge.service.XPService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserStreakRepository userStreakRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private XPService xpService;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void getAllUsers_Success() {

        User user = mock(User.class);
        UserStreak streak = mock(UserStreak.class);

        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Rahul");
        when(user.getEmail()).thenReturn("rahul@gmail.com");
        when(user.getRole()).thenReturn(User.Role.USER);
        when(user.getXp()).thenReturn(500L);

        when(streak.getUserId()).thenReturn(1L);
        when(streak.getCurrentStreak()).thenReturn(5L);
        when(streak.getLongestStreak()).thenReturn(10L);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(submissionRepository.findAll()).thenReturn(List.of());
        when(userStreakRepository.findAll()).thenReturn(List.of(streak));
        when(xpService.calculateLevel(500L)).thenReturn(2);

        List<AdminUserResponseDTO> result =
                adminUserService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());

        AdminUserResponseDTO response = result.get(0);

        assertEquals(1L, response.getUserId());
        assertEquals("Rahul", response.getName());
        assertEquals("rahul@gmail.com", response.getEmail());
        assertEquals(User.Role.USER, response.getRole());
        assertEquals(500L, response.getXp());
        assertEquals(2, response.getLevel());
        assertEquals(5L, response.getCurrentStreak());
        assertEquals(10L, response.getLongestStreak());

        verify(userRepository).findAll();
        verify(submissionRepository).findAll();
        verify(userStreakRepository).findAll();
    }

    @Test
    void getAllUsers_EmptyList() {

        when(userRepository.findAll()).thenReturn(List.of());

        List<AdminUserResponseDTO> result =
                adminUserService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verify(submissionRepository, never()).findAll();
        verify(userStreakRepository, never()).findAll();
    }

 
    @Test
    void getUserById_Success() {

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Aman");
        when(user.getEmail()).thenReturn("aman@gmail.com");
        when(user.getRole()).thenReturn(User.Role.USER);
        when(user.getXp()).thenReturn(100L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(submissionRepository.findAll()).thenReturn(List.of());
        when(userStreakRepository.findAll()).thenReturn(List.of());
        when(xpService.calculateLevel(100L)).thenReturn(1);

        AdminUserResponseDTO response =
                adminUserService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("Aman", response.getName());
        assertEquals("aman@gmail.com", response.getEmail());
        assertEquals(100L, response.getXp());
        assertEquals(0L, response.getCurrentStreak());
        assertEquals(0L, response.getLongestStreak());

        verify(userRepository).findById(1L);
    }

 
    @Test
    void getUserById_NotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminUserService.getUserById(99L)
        );

        verify(userRepository).findById(99L);
        verify(submissionRepository, never()).findAll();
    }

    @Test
    void getUserById_InvalidId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminUserService.getUserById(0L)
        );

        verify(userRepository, never()).findById(anyLong());
    }


    @Test
    void searchUsers_BlankQuery() {

        when(userRepository.findAll()).thenReturn(List.of());

        List<AdminUserResponseDTO> result =
                adminUserService.searchUsers("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
    }


    @Test
    void searchUsers_NullQuery() {

        when(userRepository.findAll()).thenReturn(List.of());

        List<AdminUserResponseDTO> result =
                adminUserService.searchUsers(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
    }


    @Test
    void searchUsers_WithQuery() {

        when(userRepository.findByNameContainingIgnoreCase("Rahul"))
                .thenReturn(List.of());

        List<AdminUserResponseDTO> result =
                adminUserService.searchUsers(" Rahul ");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, times(2))
                .findByNameContainingIgnoreCase("Rahul");
    }


    @Test
    void updateUserRole_Success() {

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Rahul");
        when(user.getEmail()).thenReturn("rahul@gmail.com");
        when(user.getRole()).thenReturn(User.Role.ADMIN);
        when(user.getXp()).thenReturn(100L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user)).thenReturn(user);

        when(submissionRepository.findAll()).thenReturn(List.of());
        when(userStreakRepository.findAll()).thenReturn(List.of());
        when(xpService.calculateLevel(100L)).thenReturn(1);

        AdminUserResponseDTO response =
                adminUserService.updateUserRole(1L, " admin ");

        assertNotNull(response);
        assertEquals(User.Role.ADMIN, response.getRole());

        verify(user).setRole("ADMIN");
        verify(userRepository).save(user);
    }


    @Test
    void updateUserRole_InvalidRole() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminUserService.updateUserRole(1L, "MANAGER")
        );

        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void updateUserRole_NullRole() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminUserService.updateUserRole(1L, null)
        );

        verify(userRepository, never()).findById(anyLong());
    }


    @Test
    void updateUserRole_BlankRole() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminUserService.updateUserRole(1L, "   ")
        );

        verify(userRepository, never()).findById(anyLong());
    }

   
    @Test
    void updateUserRole_UserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminUserService.updateUserRole(99L, "ADMIN")
        );

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void addUserXp_Success() {

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Rahul");
        when(user.getEmail()).thenReturn("rahul@gmail.com");
        when(user.getRole()).thenReturn(User.Role.USER);
        when(user.getXp()).thenReturn(600L);

        when(xpService.addXp(1L, 100L)).thenReturn(user);

        when(submissionRepository.findAll()).thenReturn(List.of());
        when(userStreakRepository.findAll()).thenReturn(List.of());
        when(xpService.calculateLevel(600L)).thenReturn(2);

        AdminUserResponseDTO response =
                adminUserService.addUserXp(1L, 100L);

        assertNotNull(response);
        assertEquals(600L, response.getXp());
        assertEquals(2, response.getLevel());

        verify(xpService).addXp(1L, 100L);
    }

   
    @Test
    void addUserXp_ZeroAmount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminUserService.addUserXp(1L, 0L)
        );

        verify(xpService, never()).addXp(anyLong(), anyLong());
    }

 
    @Test
    void addUserXp_NegativeAmount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminUserService.addUserXp(1L, -50L)
        );

        verify(xpService, never()).addXp(anyLong(), anyLong());
    }


    @Test
    void getUserSubmissions_EmptyList() {

        User user = mock(User.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(submissionRepository.findByUserId(1L))
                .thenReturn(List.of());

        List<AdminSubmissionResponseDTO> result =
                adminUserService.getUserSubmissions(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(submissionRepository).findByUserId(1L);

        verify(userRepository, never()).findAllById(anyList());
        verify(challengeRepository, never()).findAllById(anyList());
    }

    
    @Test
    void getUserSubmissions_Success() {

        User user = mock(User.class);
        Submission submission = mock(Submission.class);
        Challenge challenge = mock(Challenge.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(submissionRepository.findByUserId(1L))
                .thenReturn(List.of(submission));

        when(submission.getUserId()).thenReturn(1L);
        when(submission.getChallengeId()).thenReturn(10L);

        when(submission.getId()).thenReturn(100L);
        when(submission.getAnswer()).thenReturn("My answer");
        when(submission.getStatus()).thenReturn(null);
        when(submission.getTotalQuestions()).thenReturn(5L);
        when(submission.getCorrectAnswers()).thenReturn(5L);
        when(submission.getScore()).thenReturn(100L);
        when(submission.getFirstSubmission()).thenReturn(true);

        when(userRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(user));

        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Rahul");

        when(challengeRepository.findAllById(List.of(10L)))
                .thenReturn(List.of(challenge));

        when(challenge.getId()).thenReturn(10L);
        when(challenge.getTitle()).thenReturn("Java Basics");

        List<AdminSubmissionResponseDTO> result =
                adminUserService.getUserSubmissions(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userRepository).findAllById(List.of(1L));
        verify(challengeRepository).findAllById(List.of(10L));
    }

   
    @Test
    void getUserSubmissions_UserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminUserService.getUserSubmissions(99L)
        );

        verify(submissionRepository, never()).findByUserId(anyLong());
    }

    
    @Test
    void getUserSubmissions_InvalidUserId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminUserService.getUserSubmissions(0L)
        );

        verify(userRepository, never()).findById(anyLong());
        verify(submissionRepository, never()).findByUserId(anyLong());
    }
}
