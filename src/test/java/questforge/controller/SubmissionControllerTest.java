
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import questforge.dto.SubmissionRequestDTO;
import questforge.dto.SubmissionResponseDTO;
import questforge.dto.SubmissionStatusRequestDTO;
import questforge.service.SubmissionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {

    @Mock
    private SubmissionService submissionService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SubmissionController submissionController;

    @Test
    void createSubmission_ShouldReturnCreatedSubmission() {

        String email = "test@gmail.com";

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        SubmissionResponseDTO expected =
                mock(SubmissionResponseDTO.class);

        when(authentication.getName()).thenReturn(email);

        when(submissionService.createSubmission(request, email))
                .thenReturn(expected);

        ResponseEntity<SubmissionResponseDTO> response =
                submissionController.createSubmission(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(submissionService).createSubmission(request, email);
    }

    @Test
    void getMySubmissionById_ShouldReturnSubmission() {

        Long id = 1L;
        String email = "test@gmail.com";

        SubmissionResponseDTO expected =
                mock(SubmissionResponseDTO.class);

        when(authentication.getName()).thenReturn(email);

        when(submissionService.getMySubmissionById(id, email))
                .thenReturn(expected);

        ResponseEntity<SubmissionResponseDTO> response =
                submissionController.getMySubmissionById(id, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(submissionService).getMySubmissionById(id, email);
    }

    @Test
    void getMySubmissions_ShouldReturnUserSubmissions() {

        String email = "test@gmail.com";

        List<SubmissionResponseDTO> expected =
                List.of(mock(SubmissionResponseDTO.class),
                        mock(SubmissionResponseDTO.class));

        when(authentication.getName()).thenReturn(email);

        when(submissionService.getMySubmissions(email))
                .thenReturn(expected);

        ResponseEntity<List<SubmissionResponseDTO>> response =
                submissionController.getMySubmissions(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(submissionService).getMySubmissions(email);
    }

    @Test
    void getAllSubmissions_ShouldReturnAllSubmissions() {

        List<SubmissionResponseDTO> expected =
                List.of(mock(SubmissionResponseDTO.class));

        when(submissionService.getAllSubmissions())
                .thenReturn(expected);

        ResponseEntity<List<SubmissionResponseDTO>> response =
                submissionController.getAllSubmissions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(submissionService).getAllSubmissions();
    }

    @Test
    void updateSubmissionStatus_ShouldReturnUpdatedSubmission() {

        Long id = 1L;

        SubmissionStatusRequestDTO request =
                mock(SubmissionStatusRequestDTO.class);

        SubmissionResponseDTO expected =
                mock(SubmissionResponseDTO.class);

        when(submissionService.updateSubmissionStatus(id, request))
                .thenReturn(expected);

        ResponseEntity<SubmissionResponseDTO> response =
                submissionController.updateSubmissionStatus(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(submissionService).updateSubmissionStatus(id, request);
    }
}
