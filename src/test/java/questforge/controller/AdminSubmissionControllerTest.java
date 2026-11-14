
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.AdminSubmissionResponseDTO;
import questforge.dto.AdminSubmissionStatusRequestDTO;
import questforge.service.AdminSubmissionService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminSubmissionControllerTest {

    @Mock
    private AdminSubmissionService adminSubmissionService;

    @Mock
    private AdminSubmissionStatusRequestDTO request;

    @InjectMocks
    private AdminSubmissionController adminSubmissionController;

    private final Long SUBMISSION_ID = 101L;

    private final String STATUS = "APPROVED";


    @Test
    void getSubmissions_ShouldReturnAllSubmissions_WhenStatusIsNull() {

        List<AdminSubmissionResponseDTO> submissions = Arrays.asList(
                mock(AdminSubmissionResponseDTO.class),
                mock(AdminSubmissionResponseDTO.class)
        );

        when(adminSubmissionService.getAllSubmissions())
                .thenReturn(submissions);

        ResponseEntity<List<AdminSubmissionResponseDTO>> response =
                adminSubmissionController.getSubmissions(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertSame(submissions, response.getBody());

        verify(adminSubmissionService).getAllSubmissions();
        verify(adminSubmissionService, never())
                .getSubmissionsByStatus(anyString());
    }


    @Test
    void getSubmissions_ShouldReturnAllSubmissions_WhenStatusIsBlank() {

        List<AdminSubmissionResponseDTO> submissions =
                Collections.emptyList();

        when(adminSubmissionService.getAllSubmissions())
                .thenReturn(submissions);

        ResponseEntity<List<AdminSubmissionResponseDTO>> response =
                adminSubmissionController.getSubmissions("   ");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(adminSubmissionService).getAllSubmissions();
        verify(adminSubmissionService, never())
                .getSubmissionsByStatus(anyString());
    }


    @Test
    void getSubmissions_ShouldReturnFilteredSubmissions_WhenStatusIsProvided() {

        List<AdminSubmissionResponseDTO> submissions = Arrays.asList(
                mock(AdminSubmissionResponseDTO.class)
        );

        when(adminSubmissionService.getSubmissionsByStatus(STATUS))
                .thenReturn(submissions);

        ResponseEntity<List<AdminSubmissionResponseDTO>> response =
                adminSubmissionController.getSubmissions(STATUS);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(adminSubmissionService).getSubmissionsByStatus(STATUS);
        verify(adminSubmissionService, never()).getAllSubmissions();
    }


    @Test
    void getSubmissions_ShouldTrimStatus_WhenStatusContainsSpaces() {

        when(adminSubmissionService.getSubmissionsByStatus(STATUS))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<AdminSubmissionResponseDTO>> response =
                adminSubmissionController.getSubmissions("  APPROVED  ");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(adminSubmissionService)
                .getSubmissionsByStatus(STATUS);
    }


    @Test
    void getSubmissionById_ShouldReturnSubmission_WhenIdExists() {

        AdminSubmissionResponseDTO submission =
                mock(AdminSubmissionResponseDTO.class);

        when(adminSubmissionService.getSubmissionById(SUBMISSION_ID))
                .thenReturn(submission);

        ResponseEntity<AdminSubmissionResponseDTO> response =
                adminSubmissionController.getSubmissionById(SUBMISSION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(submission, response.getBody());

        verify(adminSubmissionService, times(1))
                .getSubmissionById(SUBMISSION_ID);
    }


    @Test
    void updateSubmissionStatus_ShouldReturnUpdatedSubmission_WhenRequestIsValid() {

        AdminSubmissionResponseDTO updatedSubmission =
                mock(AdminSubmissionResponseDTO.class);

        when(request.getStatus()).thenReturn(STATUS);

        when(adminSubmissionService.updateSubmissionStatus(
                SUBMISSION_ID, STATUS))
                .thenReturn(updatedSubmission);

        ResponseEntity<AdminSubmissionResponseDTO> response =
                adminSubmissionController.updateSubmissionStatus(
                        SUBMISSION_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(updatedSubmission, response.getBody());

        verify(adminSubmissionService, times(1))
                .updateSubmissionStatus(SUBMISSION_ID, STATUS);
    }


    @Test
    void getSubmissions_ShouldPropagateException_WhenServiceFails() {

        when(adminSubmissionService.getAllSubmissions())
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminSubmissionController.getSubmissions(null)
        );

        assertEquals("Database error", exception.getMessage());

        verify(adminSubmissionService).getAllSubmissions();
    }


    @Test
    void getSubmissionById_ShouldPropagateException_WhenServiceFails() {

        when(adminSubmissionService.getSubmissionById(SUBMISSION_ID))
                .thenThrow(new RuntimeException("Submission not found"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminSubmissionController.getSubmissionById(SUBMISSION_ID)
        );

        assertEquals("Submission not found", exception.getMessage());

        verify(adminSubmissionService).getSubmissionById(SUBMISSION_ID);
    }


    @Test
    void updateSubmissionStatus_ShouldPropagateException_WhenServiceFails() {

        when(request.getStatus()).thenReturn(STATUS);

        when(adminSubmissionService.updateSubmissionStatus(
                SUBMISSION_ID, STATUS))
                .thenThrow(new RuntimeException("Status update failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminSubmissionController.updateSubmissionStatus(
                        SUBMISSION_ID, request)
        );

        assertEquals("Status update failed", exception.getMessage());

        verify(adminSubmissionService)
                .updateSubmissionStatus(SUBMISSION_ID, STATUS);
    }
}
