
package questforge.ServiceTest;

import questforge.dto.QuestionRequestDTO;
import questforge.dto.QuestionResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Question;
import questforge.repository.ChallengeRepository;
import questforge.repository.OptionRepository;
import questforge.repository.QuestionRepository;
import questforge.service.QuestionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private QuestionService questionService;

    private QuestionRequestDTO request;

    private final Long questionId = 1L;
    private final Long challengeId = 10L;

    @BeforeEach
    void setUp() {
        request = mock(QuestionRequestDTO.class);
    }



    private Question createQuestion(
            Long id,
            Long challengeId,
            String questionText
    ) {
        Question question = new Question();

        question.setId(id);
        question.setChallengeId(challengeId);
        question.setQuestionText(questionText);

        return question;
    }

    

    @Test
    void createQuestion_Success() {

        when(request.getChallengeId()).thenReturn(challengeId);
        when(request.getQuestionText()).thenReturn("  What is Java?  ");

        when(challengeRepository.existsById(challengeId))
                .thenReturn(true);

        when(questionRepository.save(any(Question.class)))
                .thenAnswer(invocation -> {
                    Question question = invocation.getArgument(0);
                    question.setId(questionId);
                    return question;
                });

        QuestionResponseDTO result =
                questionService.createQuestion(request);

        assertNotNull(result);
        assertEquals(questionId, result.getId());
        assertEquals(challengeId, result.getChallengeId());
        assertEquals("What is Java?", result.getQuestionText());

        ArgumentCaptor<Question> captor =
                ArgumentCaptor.forClass(Question.class);

        verify(questionRepository).save(captor.capture());

        Question savedQuestion = captor.getValue();

        assertEquals(challengeId, savedQuestion.getChallengeId());
        assertEquals("What is Java?", savedQuestion.getQuestionText());

        verify(challengeRepository).existsById(challengeId);
    }



    @Test
    void createQuestion_ChallengeNotFound() {

        when(request.getChallengeId()).thenReturn(challengeId);

        when(challengeRepository.existsById(challengeId))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> questionService.createQuestion(request)
        );

        verify(questionRepository, never()).save(any());
    }


    @Test
    void createQuestion_NullChallengeId() {

        when(request.getChallengeId()).thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> questionService.createQuestion(request)
        );

        verify(challengeRepository, never()).existsById(any());
        verify(questionRepository, never()).save(any());
    }



    @Test
    void getAllQuestions_Success() {

        Question question1 = createQuestion(
                1L, 10L, "What is Java?"
        );

        Question question2 = createQuestion(
                2L, 10L, "What is Spring Boot?"
        );

        when(questionRepository.findAll())
                .thenReturn(List.of(question1, question2));

        List<QuestionResponseDTO> result =
                questionService.getAllQuestions();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("What is Java?", result.get(0).getQuestionText());

        assertEquals(2L, result.get(1).getId());
        assertEquals("What is Spring Boot?", result.get(1).getQuestionText());

        verify(questionRepository).findAll();
    }


    @Test
    void getAllQuestions_EmptyList() {

        when(questionRepository.findAll())
                .thenReturn(List.of());

        List<QuestionResponseDTO> result =
                questionService.getAllQuestions();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(questionRepository).findAll();
    }



    @Test
    void getQuestionById_Success() {

        Question question = createQuestion(
                questionId,
                challengeId,
                "What is Java?"
        );

        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(question));

        QuestionResponseDTO result =
                questionService.getQuestionById(questionId);

        assertNotNull(result);
        assertEquals(questionId, result.getId());
        assertEquals(challengeId, result.getChallengeId());
        assertEquals("What is Java?", result.getQuestionText());

        verify(questionRepository).findById(questionId);
    }



    @Test
    void getQuestionById_NotFound() {

        when(questionRepository.findById(questionId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> questionService.getQuestionById(questionId)
        );

        verify(questionRepository).findById(questionId);
    }


    @Test
    void getQuestionsByChallenge_Success() {

        Question question1 = createQuestion(
                1L, challengeId, "What is Java?"
        );

        Question question2 = createQuestion(
                2L, challengeId, "What is JVM?"
        );

        when(challengeRepository.existsById(challengeId))
                .thenReturn(true);

        when(questionRepository.findByChallengeId(challengeId))
                .thenReturn(List.of(question1, question2));

        List<QuestionResponseDTO> result =
                questionService.getQuestionsByChallenge(challengeId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("What is Java?", result.get(0).getQuestionText());
        assertEquals("What is JVM?", result.get(1).getQuestionText());

        verify(challengeRepository).existsById(challengeId);
        verify(questionRepository).findByChallengeId(challengeId);
    }


    @Test
    void getQuestionsByChallenge_ChallengeNotFound() {

        when(challengeRepository.existsById(challengeId))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> questionService.getQuestionsByChallenge(challengeId)
        );

        verify(questionRepository, never())
                .findByChallengeId(any());
    }


    @Test
    void updateQuestion_Success() {

        when(request.getChallengeId()).thenReturn(challengeId);
        when(request.getQuestionText()).thenReturn("  What is Java?  ");

        Question existingQuestion = createQuestion(
                questionId,
                challengeId,
                "Old question"
        );

        when(challengeRepository.existsById(challengeId))
                .thenReturn(true);

        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(existingQuestion));

        when(questionRepository.save(any(Question.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        QuestionResponseDTO result =
                questionService.updateQuestion(questionId, request);

        assertNotNull(result);
        assertEquals(questionId, result.getId());
        assertEquals(challengeId, result.getChallengeId());
        assertEquals("What is Java?", result.getQuestionText());

        ArgumentCaptor<Question> captor =
                ArgumentCaptor.forClass(Question.class);

        verify(questionRepository).save(captor.capture());

        Question updatedQuestion = captor.getValue();

        assertEquals(questionId, updatedQuestion.getId());
        assertEquals(challengeId, updatedQuestion.getChallengeId());
        assertEquals("What is Java?", updatedQuestion.getQuestionText());
    }



    @Test
    void updateQuestion_ChallengeNotFound() {

        when(request.getChallengeId()).thenReturn(challengeId);

        when(challengeRepository.existsById(challengeId))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> questionService.updateQuestion(questionId, request)
        );

        verify(questionRepository, never()).findById(any());
        verify(questionRepository, never()).save(any());
    }



    @Test
    void updateQuestion_QuestionNotFound() {

        when(request.getChallengeId()).thenReturn(challengeId);

        when(challengeRepository.existsById(challengeId))
                .thenReturn(true);

        when(questionRepository.findById(questionId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> questionService.updateQuestion(questionId, request)
        );

        verify(questionRepository, never()).save(any());
    }

    @Test
    void deleteQuestion_Success() {

        Question question = createQuestion(
                questionId,
                challengeId,
                "What is Java?"
        );

        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(question));

        assertDoesNotThrow(
                () -> questionService.deleteQuestion(questionId)
        );

        verify(optionRepository).deleteByQuestionId(questionId);
        verify(questionRepository).delete(question);
    }



    @Test
    void deleteQuestion_NotFound() {

        when(questionRepository.findById(questionId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> questionService.deleteQuestion(questionId)
        );

        verify(optionRepository, never()).deleteByQuestionId(any());
        verify(questionRepository, never()).delete(any());
    }
}
