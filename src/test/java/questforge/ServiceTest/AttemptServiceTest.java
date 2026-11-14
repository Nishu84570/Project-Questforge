
package questforge.ServiceTest;

import questforge.service.AttemptService;
import questforge.dto.AttemptRequestDTO;
import questforge.dto.AttemptResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Answer;
import questforge.model.Attempt;
import questforge.model.Option;
import questforge.model.Question;
import questforge.repository.AnswerRepository;
import questforge.repository.AttemptRepository;
import questforge.repository.ChallengeRepository;
import questforge.repository.OptionRepository;
import questforge.repository.QuestionRepository;
import questforge.repository.UserRepository;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttemptServiceTest {

    @Mock
    private AttemptRepository attemptRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private AttemptService attemptService;

    private Attempt attempt;
    private AttemptRequestDTO request;
    private Answer answer;
    private Question question;
    private Option option;

    @BeforeEach
    void setUp() {
        attempt = mock(Attempt.class);
        request = mock(AttemptRequestDTO.class);
        answer = mock(Answer.class);
        question = mock(Question.class);
        option = mock(Option.class);
    }



    @Test
    void createAttempt_ShouldCreateAttemptSuccessfully() {

        when(request.getUserId()).thenReturn(1L);
        when(request.getChallengeId()).thenReturn(10L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(challengeRepository.existsById(10L)).thenReturn(true);

        when(attemptRepository.save(any(Attempt.class)))
                .thenReturn(attempt);

        when(attempt.getId()).thenReturn(100L);
        when(attempt.getUserId()).thenReturn(1L);
        when(attempt.getChallengeId()).thenReturn(10L);
        when(attempt.getStartedAt()).thenReturn(LocalDateTime.now());
        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getScore()).thenReturn(0L);

        AttemptResponseDTO result =
                attemptService.createAttempt(request);

        assertNotNull(result);

        ArgumentCaptor<Attempt> captor =
                ArgumentCaptor.forClass(Attempt.class);

        verify(attemptRepository).save(captor.capture());

        Attempt savedAttempt = captor.getValue();

        assertEquals(1L, savedAttempt.getUserId());
        assertEquals(10L, savedAttempt.getChallengeId());
        assertNotNull(savedAttempt.getStartedAt());
        assertEquals(0L, savedAttempt.getScore());

        verify(userRepository).existsById(1L);
        verify(challengeRepository).existsById(10L);
    }

    @Test
    void createAttempt_ShouldThrowException_WhenUserDoesNotExist() {

        when(request.getUserId()).thenReturn(1L);

        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> attemptService.createAttempt(request)
        );

        verify(challengeRepository, never()).existsById(anyLong());
        verify(attemptRepository, never()).save(any());
    }

    @Test
    void createAttempt_ShouldThrowException_WhenChallengeDoesNotExist() {

        when(request.getUserId()).thenReturn(1L);
        when(request.getChallengeId()).thenReturn(10L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(challengeRepository.existsById(10L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> attemptService.createAttempt(request)
        );

        verify(attemptRepository, never()).save(any());
    }


    @Test
    void getAllAttempts_ShouldReturnAllAttempts() {

        when(attemptRepository.findAll())
                .thenReturn(List.of(attempt));

        when(attempt.getId()).thenReturn(1L);
        when(attempt.getUserId()).thenReturn(2L);
        when(attempt.getChallengeId()).thenReturn(10L);
        when(attempt.getStartedAt()).thenReturn(LocalDateTime.now());
        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getScore()).thenReturn(0L);

        List<AttemptResponseDTO> result =
                attemptService.getAllAttempts();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(attemptRepository).findAll();
    }

    @Test
    void getAllAttempts_ShouldReturnEmptyList_WhenNoAttemptsExist() {

        when(attemptRepository.findAll())
                .thenReturn(List.of());

        List<AttemptResponseDTO> result =
                attemptService.getAllAttempts();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(attemptRepository).findAll();
    }


    @Test
    void getAttemptById_ShouldReturnAttempt_WhenIdExists() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getId()).thenReturn(1L);
        when(attempt.getUserId()).thenReturn(2L);
        when(attempt.getChallengeId()).thenReturn(10L);
        when(attempt.getStartedAt()).thenReturn(LocalDateTime.now());
        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getScore()).thenReturn(0L);

        AttemptResponseDTO result =
                attemptService.getAttemptById(1L);

        assertNotNull(result);

        verify(attemptRepository).findById(1L);
    }

    @Test
    void getAttemptById_ShouldThrowException_WhenIdDoesNotExist() {

        when(attemptRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> attemptService.getAttemptById(99L)
        );

        verify(attemptRepository).findById(99L);
    }



    @Test
    void getAttemptsByUser_ShouldReturnAttempts() {

        when(userRepository.existsById(1L)).thenReturn(true);

        when(attemptRepository.findByUserId(1L))
                .thenReturn(List.of(attempt));

        when(attempt.getId()).thenReturn(1L);
        when(attempt.getUserId()).thenReturn(1L);
        when(attempt.getChallengeId()).thenReturn(10L);
        when(attempt.getStartedAt()).thenReturn(LocalDateTime.now());
        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getScore()).thenReturn(0L);

        List<AttemptResponseDTO> result =
                attemptService.getAttemptsByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userRepository).existsById(1L);
        verify(attemptRepository).findByUserId(1L);
    }

    @Test
    void getAttemptsByUser_ShouldReturnEmptyList_WhenNoAttemptsExist() {

        when(userRepository.existsById(1L)).thenReturn(true);

        when(attemptRepository.findByUserId(1L))
                .thenReturn(List.of());

        List<AttemptResponseDTO> result =
                attemptService.getAttemptsByUser(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAttemptsByUser_ShouldThrowException_WhenUserDoesNotExist() {

        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> attemptService.getAttemptsByUser(99L)
        );

        verify(attemptRepository, never()).findByUserId(anyLong());
    }


    @Test
    void getAttemptsByChallenge_ShouldReturnAttempts() {

        when(challengeRepository.existsById(10L)).thenReturn(true);

        when(attemptRepository.findByChallengeId(10L))
                .thenReturn(List.of(attempt));

        when(attempt.getId()).thenReturn(1L);
        when(attempt.getUserId()).thenReturn(2L);
        when(attempt.getChallengeId()).thenReturn(10L);
        when(attempt.getStartedAt()).thenReturn(LocalDateTime.now());
        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getScore()).thenReturn(0L);

        List<AttemptResponseDTO> result =
                attemptService.getAttemptsByChallenge(10L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(challengeRepository).existsById(10L);
        verify(attemptRepository).findByChallengeId(10L);
    }

    @Test
    void getAttemptsByChallenge_ShouldReturnEmptyList_WhenNoAttemptsExist() {

        when(challengeRepository.existsById(10L)).thenReturn(true);

        when(attemptRepository.findByChallengeId(10L))
                .thenReturn(List.of());

        List<AttemptResponseDTO> result =
                attemptService.getAttemptsByChallenge(10L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAttemptsByChallenge_ShouldThrowException_WhenChallengeDoesNotExist() {

        when(challengeRepository.existsById(99L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> attemptService.getAttemptsByChallenge(99L)
        );

        verify(attemptRepository, never()).findByChallengeId(anyLong());
    }


    @Test
    void completeAttempt_ShouldCompleteAttemptSuccessfully() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(answerRepository.findByAttemptId(1L))
                .thenReturn(List.of(answer));

        when(answer.getQuestionId()).thenReturn(10L);
        when(answer.getOptionId()).thenReturn(100L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(50L);
        when(question.getId()).thenReturn(10L);

        when(optionRepository.findById(100L))
                .thenReturn(Optional.of(option));

        when(option.getQuestionId()).thenReturn(10L);
        when(option.getIsCorrect()).thenReturn(true);

        when(attemptRepository.save(attempt))
                .thenReturn(attempt);

        when(attempt.getId()).thenReturn(1L);
        when(attempt.getUserId()).thenReturn(2L);
        when(attempt.getStartedAt()).thenReturn(LocalDateTime.now());
        when(attempt.getScore()).thenReturn(1L);

        AttemptResponseDTO result =
                attemptService.completeAttempt(1L);

        assertNotNull(result);

        verify(attempt).setScore(1L);
        verify(attempt).setCompletedAt(any(LocalDateTime.class));

        verify(attemptRepository).save(attempt);
    }

    @Test
    void completeAttempt_ShouldSetZeroScore_WhenNoAnswersExist() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);

        when(answerRepository.findByAttemptId(1L))
                .thenReturn(List.of());

        when(attemptRepository.save(attempt))
                .thenReturn(attempt);

        when(attempt.getId()).thenReturn(1L);
        when(attempt.getUserId()).thenReturn(2L);
        when(attempt.getChallengeId()).thenReturn(10L);
        when(attempt.getStartedAt()).thenReturn(LocalDateTime.now());
        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getScore()).thenReturn(0L);

        AttemptResponseDTO result =
                attemptService.completeAttempt(1L);

        assertNotNull(result);

        verify(attempt).setScore(0L);
        verify(attempt).setCompletedAt(any(LocalDateTime.class));

        verify(questionRepository, never()).findById(anyLong());
    }

    @Test
    void completeAttempt_ShouldNotIncreaseScore_WhenOptionIsIncorrect() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(answerRepository.findByAttemptId(1L))
                .thenReturn(List.of(answer));

        when(answer.getQuestionId()).thenReturn(10L);
        when(answer.getOptionId()).thenReturn(100L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(50L);
        when(question.getId()).thenReturn(10L);

        when(optionRepository.findById(100L))
                .thenReturn(Optional.of(option));

        when(option.getQuestionId()).thenReturn(10L);
        when(option.getIsCorrect()).thenReturn(false);

        when(attemptRepository.save(attempt))
                .thenReturn(attempt);

        when(attempt.getId()).thenReturn(1L);
        when(attempt.getUserId()).thenReturn(2L);
        when(attempt.getStartedAt()).thenReturn(LocalDateTime.now());
        when(attempt.getScore()).thenReturn(0L);

        AttemptResponseDTO result =
                attemptService.completeAttempt(1L);

        assertNotNull(result);

        verify(attempt).setScore(0L);
    }

    @Test
    void completeAttempt_ShouldThrowException_WhenAttemptDoesNotExist() {

        when(attemptRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> attemptService.completeAttempt(99L)
        );

        verify(answerRepository, never()).findByAttemptId(anyLong());
        verify(attemptRepository, never()).save(any());
    }

    @Test
    void completeAttempt_ShouldThrowException_WhenAttemptIsAlreadyCompleted() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt())
                .thenReturn(LocalDateTime.now());

        assertThrows(
                IllegalStateException.class,
                () -> attemptService.completeAttempt(1L)
        );

        verify(answerRepository, never()).findByAttemptId(anyLong());
        verify(attemptRepository, never()).save(any());
    }

    @Test
    void completeAttempt_ShouldThrowException_WhenQuestionDoesNotExist() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);

        when(answerRepository.findByAttemptId(1L))
                .thenReturn(List.of(answer));

        when(answer.getQuestionId()).thenReturn(10L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> attemptService.completeAttempt(1L)
        );

        verify(attemptRepository, never()).save(any());
    }

    @Test
    void completeAttempt_ShouldThrowException_WhenQuestionBelongsToDifferentChallenge() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(answerRepository.findByAttemptId(1L))
                .thenReturn(List.of(answer));

        when(answer.getQuestionId()).thenReturn(10L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(99L);

        assertThrows(
                IllegalStateException.class,
                () -> attemptService.completeAttempt(1L)
        );

        verify(optionRepository, never()).findById(anyLong());
        verify(attemptRepository, never()).save(any());
    }

    @Test
    void completeAttempt_ShouldThrowException_WhenOptionDoesNotExist() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(answerRepository.findByAttemptId(1L))
                .thenReturn(List.of(answer));

        when(answer.getQuestionId()).thenReturn(10L);
        when(answer.getOptionId()).thenReturn(100L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(50L);

        when(optionRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> attemptService.completeAttempt(1L)
        );

        verify(attemptRepository, never()).save(any());
    }

    @Test
    void completeAttempt_ShouldThrowException_WhenOptionBelongsToDifferentQuestion() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);
        when(attempt.getChallengeId()).thenReturn(50L);

        when(answerRepository.findByAttemptId(1L))
                .thenReturn(List.of(answer));

        when(answer.getQuestionId()).thenReturn(10L);
        when(answer.getOptionId()).thenReturn(100L);

        when(questionRepository.findById(10L))
                .thenReturn(Optional.of(question));

        when(question.getChallengeId()).thenReturn(50L);
        when(question.getId()).thenReturn(10L);

        when(optionRepository.findById(100L))
                .thenReturn(Optional.of(option));

        when(option.getQuestionId()).thenReturn(99L);

        assertThrows(
                IllegalStateException.class,
                () -> attemptService.completeAttempt(1L)
        );

        verify(attemptRepository, never()).save(any());
    }


    @Test
    void deleteAttempt_ShouldDeleteAttemptAndAnswersSuccessfully() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt()).thenReturn(null);

        when(answerRepository.findByAttemptId(1L))
                .thenReturn(List.of(answer));

        attemptService.deleteAttempt(1L);

        verify(answerRepository).findByAttemptId(1L);
        verify(answerRepository).deleteAll(List.of(answer));
        verify(attemptRepository).delete(attempt);
    }

    @Test
    void deleteAttempt_ShouldThrowException_WhenAttemptDoesNotExist() {

        when(attemptRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> attemptService.deleteAttempt(99L)
        );

        verify(answerRepository, never()).findByAttemptId(anyLong());
        verify(attemptRepository, never()).delete(any());
    }

    @Test
    void deleteAttempt_ShouldThrowException_WhenAttemptIsCompleted() {

        when(attemptRepository.findById(1L))
                .thenReturn(Optional.of(attempt));

        when(attempt.getCompletedAt())
                .thenReturn(LocalDateTime.now());

        assertThrows(
                IllegalStateException.class,
                () -> attemptService.deleteAttempt(1L)
        );

        verify(answerRepository, never()).findByAttemptId(anyLong());
        verify(attemptRepository, never()).delete(any());
    }
}
