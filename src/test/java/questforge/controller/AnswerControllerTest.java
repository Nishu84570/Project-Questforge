
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import questforge.dto.AnswerRequestDTO;
import questforge.dto.AnswerResponseDTO;
import questforge.service.AnswerService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerControllerTest {

    @Mock
    private AnswerService answerService;

    @Mock
    private AnswerRequestDTO request;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AnswerController answerController;

    private final Long ANSWER_ID = 101L;
    private final Long ATTEMPT_ID = 201L;


    @Test
    void createAnswer_ShouldReturnCreatedAnswer() {

        AnswerResponseDTO answer = mock(AnswerResponseDTO.class);

        when(answerService.createAnswer(request)).thenReturn(answer);

        ResponseEntity<AnswerResponseDTO> response =
                answerController.createAnswer(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(answer, response.getBody());

        verify(answerService).createAnswer(request);
    }


    @Test
    void getAllAnswers_ShouldReturnAllAnswers() {

        List<AnswerResponseDTO> answers = Arrays.asList(
                mock(AnswerResponseDTO.class),
                mock(AnswerResponseDTO.class)
        );

        when(answerService.getAllAnswers()).thenReturn(answers);

        ResponseEntity<List<AnswerResponseDTO>> response =
                answerController.getAllAnswers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(answers, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(answerService).getAllAnswers();
    }


    @Test
    void getAllAnswers_ShouldReturnEmptyList_WhenNoAnswersExist() {

        when(answerService.getAllAnswers())
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<AnswerResponseDTO>> response =
                answerController.getAllAnswers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(answerService).getAllAnswers();
    }

    @Test
    void getAnswerById_ShouldReturnAnswer_WhenAnswerExists() {

        AnswerResponseDTO answer = mock(AnswerResponseDTO.class);

        when(answerService.getAnswerById(ANSWER_ID))
                .thenReturn(answer);

        ResponseEntity<AnswerResponseDTO> response =
                answerController.getAnswerById(ANSWER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(answer, response.getBody());

        verify(answerService).getAnswerById(ANSWER_ID);
    }


    @Test
    void getAnswersByAttempt_ShouldReturnAnswers() {

        List<AnswerResponseDTO> answers = Collections.singletonList(
                mock(AnswerResponseDTO.class)
        );

        when(answerService.getAnswersByAttempt(ATTEMPT_ID))
                .thenReturn(answers);

        ResponseEntity<List<AnswerResponseDTO>> response =
                answerController.getAnswersByAttempt(ATTEMPT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(answers, response.getBody());

        verify(answerService).getAnswersByAttempt(ATTEMPT_ID);
    }

    @Test
    void updateAnswer_ShouldReturnUpdatedAnswer() {

        AnswerResponseDTO updatedAnswer =
                mock(AnswerResponseDTO.class);

        when(answerService.updateAnswer(ANSWER_ID, request))
                .thenReturn(updatedAnswer);

        ResponseEntity<AnswerResponseDTO> response =
                answerController.updateAnswer(
                        ANSWER_ID, request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedAnswer, response.getBody());

        verify(answerService).updateAnswer(ANSWER_ID, request);
    }


    @Test
    void deleteAnswer_ShouldReturnNoContent_WhenDeletionSucceeds() {

        doNothing().when(answerService).deleteAnswer(ANSWER_ID);

        ResponseEntity<Void> response =
                answerController.deleteAnswer(ANSWER_ID, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(answerService, times(1)).deleteAnswer(ANSWER_ID);
    }


    @Test
    void createAnswer_ShouldPropagateException_WhenServiceFails() {

        when(answerService.createAnswer(request))
                .thenThrow(new RuntimeException("Answer creation failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> answerController.createAnswer(request, authentication)
        );

        assertEquals("Answer creation failed", exception.getMessage());

        verify(answerService).createAnswer(request);
    }


    @Test
    void deleteAnswer_ShouldPropagateException_WhenServiceFails() {

        doThrow(new RuntimeException("Answer deletion failed"))
                .when(answerService).deleteAnswer(ANSWER_ID);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> answerController.deleteAnswer(
                        ANSWER_ID, authentication)
        );

        assertEquals("Answer deletion failed", exception.getMessage());

        verify(answerService).deleteAnswer(ANSWER_ID);
    }
}
