
package questforge.ServiceTest;

import questforge.service.AnswerService;
import questforge.dto.AnswerRequestDTO;
import questforge.dto.AnswerResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Answer;
import questforge.model.Attempt;
import questforge.model.Option;
import questforge.model.Question;
import questforge.repository.AnswerRepository;
import questforge.repository.AttemptRepository;
import questforge.repository.OptionRepository;
import questforge.repository.QuestionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private AttemptRepository attemptRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private AnswerService answerService;

    private Answer answer;
    private Attempt attempt;
    private Question question;
    private Option option;
    private AnswerRequestDTO request;

    @BeforeEach
    void setUp() {
        answer = mock(Answer.class);
        attempt = mock(Attempt.class);
        question = mock(Question.class);
        option = mock(Option.class);
        request = mock(AnswerRequestDTO.class);
    }

   

    @Test
    void createAnswer_ShouldCreateAnswerSuccessfully() {

        when(request.getAttemptId()).thenReturn(1L);
        when(request.getQuestionId()).thenReturn(10L);
        when(request.getOptionId()).thenReturn(100L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(50L);
        when(question.getId()).thenReturn(10L);

        when(answerRepository.existsByAttemptIdAndQuestionId(1L, 10L))
                .thenReturn(false);

        when(optionRepository.findById(100L))
                .thenReturn(Optional.of(option));

        when(option.getQuestionId()).thenReturn(10L);

        when(answer.getId()).thenReturn(1L);
        when(answer.getAttemptId()).thenReturn(1L);
        when(answer.getQuestionId()).thenReturn(10L);
        when(answer.getOptionId()).thenReturn(100L);

        when(answerRepository.save(any(Answer.class)))
                .thenReturn(answer);

        AnswerResponseDTO result =
                answerService.createAnswer(request);

        assertNotNull(result);

        ArgumentCaptor<Answer> captor =
                ArgumentCaptor.forClass(Answer.class);

        verify(answerRepository).save(captor.capture());

        Answer savedAnswer = captor.getValue();

        assertEquals(1L, savedAnswer.getAttemptId());
        assertEquals(10L, savedAnswer.getQuestionId());
        assertEquals(100L, savedAnswer.getOptionId());
    }

    @Test
    void createAnswer_ShouldThrowException_WhenAttemptDoesNotExist() {

        when(request.getAttemptId()).thenReturn(1L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> answerService.createAnswer(request)
        );

        verify(questionRepository, never()).findById(anyLong());
        verify(answerRepository, never()).save(any());
    }

    @Test
    void createAnswer_ShouldThrowException_WhenAttemptIsCompleted() {

        when(request.getAttemptId()).thenReturn(1L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt())
                .thenReturn(LocalDateTime.now());

        assertThrows(
                IllegalStateException.class,
                () -> answerService.createAnswer(request)
        );

        verify(questionRepository, never()).findById(anyLong());
        verify(answerRepository, never()).save(any());
    }

    @Test
    void createAnswer_ShouldThrowException_WhenQuestionDoesNotExist() {

        when(request.getAttemptId()).thenReturn(1L);
        when(request.getQuestionId()).thenReturn(10L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> answerService.createAnswer(request)
        );

        verify(answerRepository, never()).save(any());
    }

    @Test
    void createAnswer_ShouldThrowException_WhenQuestionBelongsToDifferentChallenge() {

        when(request.getAttemptId()).thenReturn(1L);
        when(request.getQuestionId()).thenReturn(10L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(99L);

        assertThrows(
                IllegalStateException.class,
                () -> answerService.createAnswer(request)
        );

        verify(answerRepository, never()).save(any());
    }

    @Test
    void createAnswer_ShouldThrowException_WhenAnswerAlreadyExists() {

        when(request.getAttemptId()).thenReturn(1L);
        when(request.getQuestionId()).thenReturn(10L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(50L);

        when(answerRepository.existsByAttemptIdAndQuestionId(1L, 10L))
                .thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> answerService.createAnswer(request)
        );

        verify(optionRepository, never()).findById(anyLong());
        verify(answerRepository, never()).save(any());
    }

    @Test
    void createAnswer_ShouldThrowException_WhenOptionDoesNotExist() {

        when(request.getAttemptId()).thenReturn(1L);
        when(request.getQuestionId()).thenReturn(10L);
        when(request.getOptionId()).thenReturn(100L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(50L);

        when(answerRepository.existsByAttemptIdAndQuestionId(1L, 10L))
                .thenReturn(false);

        when(optionRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> answerService.createAnswer(request)
        );

        verify(answerRepository, never()).save(any());
    }

    @Test
    void createAnswer_ShouldThrowException_WhenOptionBelongsToDifferentQuestion() {

        when(request.getAttemptId()).thenReturn(1L);
        when(request.getQuestionId()).thenReturn(10L);
        when(request.getOptionId()).thenReturn(100L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(50L);
        when(question.getId()).thenReturn(10L);

        when(answerRepository.existsByAttemptIdAndQuestionId(1L, 10L))
                .thenReturn(false);

        when(optionRepository.findById(100L))
                .thenReturn(Optional.of(option));

        when(option.getQuestionId()).thenReturn(99L);

        assertThrows(
                IllegalStateException.class,
                () -> answerService.createAnswer(request)
        );

        verify(answerRepository, never()).save(any());
    }

    

    @Test
    void getAllAnswers_ShouldReturnAllAnswers() {

        when(answerRepository.findAll())
                .thenReturn(List.of(answer));

        when(answer.getId()).thenReturn(1L);
        when(answer.getAttemptId()).thenReturn(1L);
        when(answer.getQuestionId()).thenReturn(10L);
        when(answer.getOptionId()).thenReturn(100L);

        List<AnswerResponseDTO> result =
                answerService.getAllAnswers();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(answerRepository).findAll();
    }

    @Test
    void getAllAnswers_ShouldReturnEmptyList_WhenNoAnswersExist() {

        when(answerRepository.findAll())
                .thenReturn(List.of());

        List<AnswerResponseDTO> result =
                answerService.getAllAnswers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(answerRepository).findAll();
    }

    

    @Test
    void getAnswerById_ShouldReturnAnswer_WhenIdExists() {

        when(answerRepository.findById(1L))
                .thenReturn(Optional.of(answer));

        when(answer.getId()).thenReturn(1L);
        when(answer.getAttemptId()).thenReturn(1L);
        when(answer.getQuestionId()).thenReturn(10L);
        when(answer.getOptionId()).thenReturn(100L);

        AnswerResponseDTO result =
                answerService.getAnswerById(1L);

        assertNotNull(result);

        verify(answerRepository).findById(1L);
    }

    @Test
    void getAnswerById_ShouldThrowException_WhenIdDoesNotExist() {

        when(answerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> answerService.getAnswerById(99L)
        );

        verify(answerRepository).findById(99L);
    }

    

    @Test
    void getAnswersByAttempt_ShouldReturnAnswers() {

        when(attemptRepository.existsById(1L))
                .thenReturn(true);

        when(answerRepository.findByAttemptId(1L))
                .thenReturn(List.of(answer));

        when(answer.getId()).thenReturn(1L);
        when(answer.getAttemptId()).thenReturn(1L);
        when(answer.getQuestionId()).thenReturn(10L);
        when(answer.getOptionId()).thenReturn(100L);

        List<AnswerResponseDTO> result =
                answerService.getAnswersByAttempt(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(attemptRepository).existsById(1L);
        verify(answerRepository).findByAttemptId(1L);
    }

    @Test
    void getAnswersByAttempt_ShouldReturnEmptyList_WhenNoAnswersExist() {

        when(attemptRepository.existsById(1L))
                .thenReturn(true);

        when(answerRepository.findByAttemptId(1L))
                .thenReturn(List.of());

        List<AnswerResponseDTO> result =
                answerService.getAnswersByAttempt(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAnswersByAttempt_ShouldThrowException_WhenAttemptDoesNotExist() {

        when(attemptRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> answerService.getAnswersByAttempt(99L)
        );

        verify(answerRepository, never()).findByAttemptId(anyLong());
    }

    

    @Test
    void updateAnswer_ShouldUpdateAnswerSuccessfully() {

        when(answerRepository.findById(1L))
                .thenReturn(Optional.of(answer));

        when(answer.getAttemptId()).thenReturn(1L);
        when(answer.getQuestionId()).thenReturn(10L);

        when(request.getAttemptId()).thenReturn(1L);
        when(request.getQuestionId()).thenReturn(10L);
        when(request.getOptionId()).thenReturn(100L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(50L);
        when(question.getId()).thenReturn(10L);

        when(optionRepository.findById(100L))
                .thenReturn(Optional.of(option));

        when(option.getQuestionId()).thenReturn(10L);

        when(answerRepository.save(answer))
                .thenReturn(answer);

        when(answer.getId()).thenReturn(1L);
        when(answer.getOptionId()).thenReturn(100L);

        AnswerResponseDTO result =
                answerService.updateAnswer(1L, request);

        assertNotNull(result);

        verify(answer).setAttemptId(1L);
        verify(answer).setQuestionId(10L);
        verify(answer).setOptionId(100L);

        verify(answerRepository).save(answer);

        verify(answerRepository, never())
                .existsByAttemptIdAndQuestionId(anyLong(), anyLong());
    }

    @Test
    void updateAnswer_ShouldThrowException_WhenAnswerDoesNotExist() {

        when(answerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> answerService.updateAnswer(99L, request)
        );

        verify(answerRepository, never()).save(any());
    }

    @Test
    void updateAnswer_ShouldThrowException_WhenOriginalAttemptIsCompleted() {

        when(answerRepository.findById(1L))
                .thenReturn(Optional.of(answer));

        when(answer.getAttemptId()).thenReturn(1L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt())
                .thenReturn(LocalDateTime.now());

        assertThrows(
                IllegalStateException.class,
                () -> answerService.updateAnswer(1L, request)
        );

        verify(answerRepository, never()).save(any());
    }

    @Test
    void updateAnswer_ShouldThrowException_WhenTargetAttemptDoesNotExist() {

        when(answerRepository.findById(1L))
                .thenReturn(Optional.of(answer));

        when(answer.getAttemptId()).thenReturn(1L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);

        when(request.getAttemptId()).thenReturn(2L);

        when(attemptRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> answerService.updateAnswer(1L, request)
        );

        verify(answerRepository, never()).save(any());
    }

    @Test
    void updateAnswer_ShouldThrowException_WhenTargetAttemptIsCompleted() {

        Attempt targetAttempt = mock(Attempt.class);

        when(answerRepository.findById(1L))
                .thenReturn(Optional.of(answer));

        when(answer.getAttemptId()).thenReturn(1L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);

        when(request.getAttemptId()).thenReturn(2L);

        when(attemptRepository.findById(2L))
                .thenReturn(Optional.of(targetAttempt));

        when(targetAttempt.getCompletedAt())
                .thenReturn(LocalDateTime.now());

        assertThrows(
                IllegalStateException.class,
                () -> answerService.updateAnswer(1L, request)
        );

        verify(questionRepository, never()).findById(anyLong());
        verify(answerRepository, never()).save(any());
    }

    
@Test
void updateAnswer_ShouldThrowException_WhenDuplicateAnswerExists() {

    Attempt targetAttempt = mock(Attempt.class);

    when(answerRepository.findById(1L))
            .thenReturn(Optional.of(answer));

    when(answer.getAttemptId()).thenReturn(1L);
    when(request.getAttemptId()).thenReturn(2L);
    when(request.getQuestionId()).thenReturn(20L);

    when(attemptRepository.findById(1L))
            .thenReturn(Optional.of(attempt));

    when(attempt.getCompletedAt()).thenReturn(null);

    when(attemptRepository.findById(2L))
            .thenReturn(Optional.of(targetAttempt));

    when(targetAttempt.getCompletedAt()).thenReturn(null);
    when(targetAttempt.getChallengeId()).thenReturn(50L);

    when(questionRepository.findById(20L))
            .thenReturn(Optional.of(question));

    when(question.getChallengeId()).thenReturn(50L);

    when(answerRepository.existsByAttemptIdAndQuestionId(2L, 20L))
            .thenReturn(true);

    assertThrows(
            IllegalStateException.class,
            () -> answerService.updateAnswer(1L, request)
    );

    verify(answerRepository)
            .existsByAttemptIdAndQuestionId(2L, 20L);

    verify(optionRepository, never()).findById(anyLong());
    verify(answerRepository, never()).save(any());
}


    @Test
    void updateAnswer_ShouldThrowException_WhenOptionBelongsToDifferentQuestion() {

        when(answerRepository.findById(1L))
                .thenReturn(Optional.of(answer));

        when(answer.getAttemptId()).thenReturn(1L);
        when(answer.getQuestionId()).thenReturn(10L);

        when(request.getAttemptId()).thenReturn(1L);
        when(request.getQuestionId()).thenReturn(10L);
        when(request.getOptionId()).thenReturn(100L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(50L);
        when(question.getId()).thenReturn(10L);

        when(optionRepository.findById(100L))
                .thenReturn(Optional.of(option));

        when(option.getQuestionId()).thenReturn(99L);

        assertThrows(
                IllegalStateException.class,
                () -> answerService.updateAnswer(1L, request)
        );

        verify(answerRepository, never()).save(any());
    }

    

    @Test
    void deleteAnswer_ShouldDeleteAnswerSuccessfully() {

        when(answerRepository.findById(1L))
                .thenReturn(Optional.of(answer));

        when(answer.getAttemptId()).thenReturn(1L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);

        answerService.deleteAnswer(1L);

        verify(answerRepository).findById(1L);
        verify(attemptRepository).findById(1L);
        verify(answerRepository).delete(answer);
    }

    @Test
    void deleteAnswer_ShouldThrowException_WhenAnswerDoesNotExist() {

        when(answerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> answerService.deleteAnswer(99L)
        );

        verify(answerRepository, never()).delete(any());
    }

    @Test
    void deleteAnswer_ShouldThrowException_WhenAttemptDoesNotExist() {

        when(answerRepository.findById(1L))
                .thenReturn(Optional.of(answer));

        when(answer.getAttemptId()).thenReturn(1L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> answerService.deleteAnswer(1L)
        );

        verify(answerRepository, never()).delete(any());
    }

    @Test
    void deleteAnswer_ShouldThrowException_WhenAttemptIsCompleted() {

        when(answerRepository.findById(1L))
                .thenReturn(Optional.of(answer));

        when(answer.getAttemptId()).thenReturn(1L);

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt())
                .thenReturn(LocalDateTime.now());

        assertThrows(
                IllegalStateException.class,
                () -> answerService.deleteAnswer(1L)
        );

        verify(answerRepository, never()).delete(any());
    }
}
