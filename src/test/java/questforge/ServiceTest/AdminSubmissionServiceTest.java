
package questforge.ServiceTest;

import questforge.service.AdminSubmissionService;
import questforge.dto.AdminSubmissionResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.model.User;
import questforge.repository.ChallengeRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminSubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private AdminSubmissionService adminSubmissionService;

    private Submission submission;
    private User user;
    private Challenge challenge;

    @BeforeEach
    void setUp() {
        submission = mock(Submission.class);
        user = mock(User.class);
        challenge = mock(Challenge.class);
    }


    private void mockSubmissionData() {

        when(submission.getId()).thenReturn(1L);
        when(submission.getUserId()).thenReturn(10L);
        when(submission.getChallengeId()).thenReturn(100L);
        when(submission.getStatus()).thenReturn("PENDING");

        when(user.getId()).thenReturn(10L);
        when(user.getName()).thenReturn("Nishu");

        when(challenge.getId()).thenReturn(100L);
        when(challenge.getTitle()).thenReturn("Java Challenge");

        when(userRepository.findAllById(List.of(10L)))
                .thenReturn(List.of(user));

        when(challengeRepository.findAllById(List.of(100L)))
                .thenReturn(List.of(challenge));
    }

    

    @Test
    void getAllSubmissions_ShouldReturnAllSubmissions() {

        mockSubmissionData();

        when(submissionRepository.findAll())
                .thenReturn(List.of(submission));

        List<AdminSubmissionResponseDTO> result =
                adminSubmissionService.getAllSubmissions();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(submissionRepository).findAll();
        verify(userRepository).findAllById(List.of(10L));
        verify(challengeRepository).findAllById(List.of(100L));
    }

    @Test
    void getAllSubmissions_ShouldReturnEmptyList_WhenNoSubmissionsExist() {

        when(submissionRepository.findAll())
                .thenReturn(List.of());

        when(userRepository.findAllById(List.of()))
                .thenReturn(List.of());

        when(challengeRepository.findAllById(List.of()))
                .thenReturn(List.of());

        List<AdminSubmissionResponseDTO> result =
                adminSubmissionService.getAllSubmissions();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(submissionRepository).findAll();
    }



    @Test
    void getSubmissionsByStatus_ShouldReturnPendingSubmissions() {

        mockSubmissionData();

        when(submissionRepository.findAll())
                .thenReturn(List.of(submission));

        List<AdminSubmissionResponseDTO> result =
                adminSubmissionService.getSubmissionsByStatus("PENDING");

        assertEquals(1, result.size());

        verify(submissionRepository).findAll();
    }

    @Test
    void getSubmissionsByStatus_ShouldIgnoreCaseAndSpaces() {

        mockSubmissionData();

        when(submissionRepository.findAll())
                .thenReturn(List.of(submission));

        List<AdminSubmissionResponseDTO> result =
                adminSubmissionService.getSubmissionsByStatus(" pending ");

        assertEquals(1, result.size());
    }

    @Test
    void getSubmissionsByStatus_ShouldReturnEmptyList_WhenStatusDoesNotMatch() {

        when(submission.getStatus()).thenReturn("PENDING");

        when(submissionRepository.findAll())
                .thenReturn(List.of(submission));

        when(userRepository.findAllById(List.of()))
                .thenReturn(List.of());

        when(challengeRepository.findAllById(List.of()))
                .thenReturn(List.of());

        List<AdminSubmissionResponseDTO> result =
                adminSubmissionService.getSubmissionsByStatus("APPROVED");

        assertTrue(result.isEmpty());
    }

    @Test
    void getSubmissionsByStatus_ShouldReturnAll_WhenStatusIsNull() {

        mockSubmissionData();

        when(submissionRepository.findAll())
                .thenReturn(List.of(submission));

        List<AdminSubmissionResponseDTO> result =
                adminSubmissionService.getSubmissionsByStatus(null);

        assertEquals(1, result.size());
    }

    @Test
    void getSubmissionsByStatus_ShouldReturnAll_WhenStatusIsBlank() {

        mockSubmissionData();

        when(submissionRepository.findAll())
                .thenReturn(List.of(submission));

        List<AdminSubmissionResponseDTO> result =
                adminSubmissionService.getSubmissionsByStatus("   ");

        assertEquals(1, result.size());
    }

    @Test
    void getSubmissionsByStatus_ShouldThrowException_WhenStatusIsInvalid() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminSubmissionService.getSubmissionsByStatus("COMPLETED")
        );

        verify(submissionRepository, never()).findAll();
    }

    @Test
    void getSubmissionsByStatus_ShouldIgnoreNullSubmissionStatus() {

        Submission nullStatusSubmission = mock(Submission.class);

        when(nullStatusSubmission.getStatus()).thenReturn(null);

        when(submissionRepository.findAll())
                .thenReturn(List.of(nullStatusSubmission));

        when(userRepository.findAllById(List.of()))
                .thenReturn(List.of());

        when(challengeRepository.findAllById(List.of()))
                .thenReturn(List.of());

        List<AdminSubmissionResponseDTO> result =
                adminSubmissionService.getSubmissionsByStatus("PENDING");

        assertTrue(result.isEmpty());
    }

    

    @Test
    void getSubmissionById_ShouldReturnSubmission_WhenIdExists() {

        mockSubmissionData();

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.of(submission));

        AdminSubmissionResponseDTO result =
                adminSubmissionService.getSubmissionById(1L);

        assertNotNull(result);

        verify(submissionRepository).findById(1L);
    }

    @Test
    void getSubmissionById_ShouldThrowException_WhenIdDoesNotExist() {

        when(submissionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminSubmissionService.getSubmissionById(99L)
        );

        verify(submissionRepository).findById(99L);
        verify(submissionRepository, never()).save(any());
    }

    

    @Test
    void updateSubmissionStatus_ShouldApproveSubmission() {

        mockSubmissionData();

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.of(submission));

        when(submissionRepository.save(submission))
                .thenReturn(submission);

        AdminSubmissionResponseDTO result =
                adminSubmissionService.updateSubmissionStatus(1L, "APPROVED");

        assertNotNull(result);

        verify(submission).setStatus("APPROVED");
        verify(submissionRepository).save(submission);
    }

    @Test
    void updateSubmissionStatus_ShouldRejectSubmission() {

        mockSubmissionData();

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.of(submission));

        when(submissionRepository.save(submission))
                .thenReturn(submission);

        AdminSubmissionResponseDTO result =
                adminSubmissionService.updateSubmissionStatus(1L, "REJECTED");

        assertNotNull(result);

        verify(submission).setStatus("REJECTED");
        verify(submissionRepository).save(submission);
    }

    @Test
    void updateSubmissionStatus_ShouldNormalizeStatus() {

        mockSubmissionData();

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.of(submission));

        when(submissionRepository.save(submission))
                .thenReturn(submission);

        adminSubmissionService.updateSubmissionStatus(1L, " approved ");

        verify(submission).setStatus("APPROVED");
    }

    @Test
    void updateSubmissionStatus_ShouldThrowException_WhenStatusIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminSubmissionService.updateSubmissionStatus(1L, null)
        );

        verify(submissionRepository, never()).findById(anyLong());
    }

    @Test
    void updateSubmissionStatus_ShouldThrowException_WhenStatusIsBlank() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminSubmissionService.updateSubmissionStatus(1L, "  ")
        );

        verify(submissionRepository, never()).findById(anyLong());
    }

    @Test
    void updateSubmissionStatus_ShouldThrowException_WhenStatusIsPending() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminSubmissionService.updateSubmissionStatus(1L, "PENDING")
        );

        verify(submissionRepository, never()).findById(anyLong());
    }

    @Test
    void updateSubmissionStatus_ShouldThrowException_WhenStatusIsInvalid() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminSubmissionService.updateSubmissionStatus(1L, "COMPLETED")
        );

        verify(submissionRepository, never()).findById(anyLong());
    }

    @Test
    void updateSubmissionStatus_ShouldThrowException_WhenSubmissionNotFound() {

        when(submissionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminSubmissionService.updateSubmissionStatus(99L, "APPROVED")
        );

        verify(submissionRepository, never()).save(any());
    }



    @Test
    void getAllSubmissions_ShouldHandleDeletedUserAndChallenge() {

        when(submission.getId()).thenReturn(1L);
        when(submission.getUserId()).thenReturn(10L);
        when(submission.getChallengeId()).thenReturn(100L);

        when(submissionRepository.findAll())
                .thenReturn(List.of(submission));

        when(userRepository.findAllById(List.of(10L)))
                .thenReturn(List.of());

        when(challengeRepository.findAllById(List.of(100L)))
                .thenReturn(List.of());

        List<AdminSubmissionResponseDTO> result =
                adminSubmissionService.getAllSubmissions();

        assertEquals(1, result.size());
        assertNotNull(result.get(0));
    }

    @Test
    void getAllSubmissions_ShouldHandleNullUserAndChallengeIds() {

        Submission nullIdSubmission = mock(Submission.class);

        when(nullIdSubmission.getUserId()).thenReturn(null);
        when(nullIdSubmission.getChallengeId()).thenReturn(null);

        when(submissionRepository.findAll())
                .thenReturn(List.of(nullIdSubmission));

        when(userRepository.findAllById(List.of()))
                .thenReturn(List.of());

        when(challengeRepository.findAllById(List.of()))
                .thenReturn(List.of());

        List<AdminSubmissionResponseDTO> result =
                adminSubmissionService.getAllSubmissions();

        assertEquals(1, result.size());

        verify(userRepository).findAllById(List.of());
        verify(challengeRepository).findAllById(List.of());
    }
}
