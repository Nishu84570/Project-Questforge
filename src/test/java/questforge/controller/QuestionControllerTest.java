
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.QuestionRequestDTO;
import questforge.dto.QuestionResponseDTO;
import questforge.service.QuestionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionControllerTest {

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    @Test
    void createQuestion_ShouldReturnCreatedQuestion() {

        QuestionRequestDTO request = mock(QuestionRequestDTO.class);
        QuestionResponseDTO expected = mock(QuestionResponseDTO.class);

        when(questionService.createQuestion(request))
                .thenReturn(expected);

        ResponseEntity<QuestionResponseDTO> response =
                questionController.createQuestion(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(questionService).createQuestion(request);
    }

    @Test
    void getAllQuestions_ShouldReturnAllQuestions() {

        List<QuestionResponseDTO> expected =
                List.of(mock(QuestionResponseDTO.class),
                        mock(QuestionResponseDTO.class));

        when(questionService.getAllQuestions())
                .thenReturn(expected);

        ResponseEntity<List<QuestionResponseDTO>> response =
                questionController.getAllQuestions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(questionService).getAllQuestions();
    }

    @Test
    void getQuestionById_ShouldReturnQuestion() {

        Long id = 1L;

        QuestionResponseDTO expected = mock(QuestionResponseDTO.class);

        when(questionService.getQuestionById(id))
                .thenReturn(expected);

        ResponseEntity<QuestionResponseDTO> response =
                questionController.getQuestionById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(questionService).getQuestionById(id);
    }

    @Test
    void getQuestionsByChallenge_ShouldReturnQuestions() {

        Long challengeId = 10L;

        List<QuestionResponseDTO> expected =
                List.of(mock(QuestionResponseDTO.class));

        when(questionService.getQuestionsByChallenge(challengeId))
                .thenReturn(expected);

        ResponseEntity<List<QuestionResponseDTO>> response =
                questionController.getQuestionsByChallenge(challengeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(questionService).getQuestionsByChallenge(challengeId);
    }

    @Test
    void updateQuestion_ShouldReturnUpdatedQuestion() {

        Long id = 1L;

        QuestionRequestDTO request = mock(QuestionRequestDTO.class);
        QuestionResponseDTO expected = mock(QuestionResponseDTO.class);

        when(questionService.updateQuestion(id, request))
                .thenReturn(expected);

        ResponseEntity<QuestionResponseDTO> response =
                questionController.updateQuestion(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(questionService).updateQuestion(id, request);
    }

    @Test
    void deleteQuestion_ShouldReturnNoContent() {

        Long id = 1L;

        ResponseEntity<Void> response =
                questionController.deleteQuestion(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(questionService).deleteQuestion(id);
    }
}
