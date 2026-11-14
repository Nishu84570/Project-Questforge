
package questforge.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import questforge.dto.SubmissionAnswerDTO;
import questforge.dto.SubmissionRequestDTO;
import questforge.dto.SubmissionResponseDTO;
import questforge.dto.SubmissionStatusRequestDTO;

import questforge.exception.ResourceNotFoundException;

import questforge.model.Challenge;
import questforge.model.Option;
import questforge.model.Question;
import questforge.model.Submission;
import questforge.model.User;

import questforge.repository.ChallengeRepository;
import questforge.repository.OptionRepository;
import questforge.repository.QuestionRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;

import questforge.service.AchievementService;
import questforge.service.DailyQuestService;
import questforge.service.StreakService;
import questforge.service.SubmissionService;
import questforge.service.WeeklyQuestService;
import questforge.service.XPService;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private AchievementService achievementService;

    @Mock
    private StreakService streakService;

    @Mock
    private DailyQuestService dailyQuestService;

    @Mock
    private XPService xpService;

    @Mock
    private WeeklyQuestService weeklyQuestService;

    @InjectMocks
    private SubmissionService submissionService;

    private User user;
    private Challenge challenge;
    private Question question1;
    private Question question2;

    private final String email = "test@gmail.com";


    @BeforeEach
    void setUp() {

        user = mock(User.class);
        challenge = mock(Challenge.class);

        question1 = mock(Question.class);
        question2 = mock(Question.class);
    }

    private void stubUser() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(user.getId()).thenReturn(1L);
        when(user.getXp()).thenReturn(100L);
    }


    private void stubChallenge() {

        when(challengeRepository.findById(10L))
                .thenReturn(Optional.of(challenge));

        when(challenge.getId()).thenReturn(10L);
        when(challenge.getDifficulty()).thenReturn("EASY");
    }


    private void stubQuestions() {

        when(question1.getId()).thenReturn(101L);
        when(question2.getId()).thenReturn(102L);

        when(questionRepository.findByChallengeId(10L))
                .thenReturn(List.of(question1, question2));
    }


    private SubmissionRequestDTO validRequest() {

        SubmissionAnswerDTO answer1 =
                mock(SubmissionAnswerDTO.class);

        SubmissionAnswerDTO answer2 =
                mock(SubmissionAnswerDTO.class);

        when(answer1.getQuestionId()).thenReturn(101L);
        when(answer1.getOptionId()).thenReturn(201L);

        when(answer2.getQuestionId()).thenReturn(102L);
        when(answer2.getOptionId()).thenReturn(202L);

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(10L);

        when(request.getAnswers())
                .thenReturn(List.of(answer1, answer2));

        return request;
    }

    private void stubValidSubmission() {

        stubUser();
        stubChallenge();
        stubQuestions();

        Option option1 = mock(Option.class);
        Option option2 = mock(Option.class);

        when(optionRepository.findById(201L))
                .thenReturn(Optional.of(option1));

        when(optionRepository.findById(202L))
                .thenReturn(Optional.of(option2));

        when(option1.getQuestionId()).thenReturn(101L);
        when(option2.getQuestionId()).thenReturn(102L);

        when(option1.getIsCorrect()).thenReturn(true);
        when(option2.getIsCorrect()).thenReturn(false);

        when(option1.getOptionText()).thenReturn("Option A");
        when(option2.getOptionText()).thenReturn("Option B");

        when(submissionRepository
                .findByUserIdAndChallengeIdOrderBySubmittedAtAsc(1L, 10L))
                .thenReturn(List.of());

        when(submissionRepository.save(any(Submission.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(submissionRepository.findByUserId(1L))
                .thenReturn(List.of());

        when(xpService.awardChallengeXp(1L, "EASY"))
                .thenReturn(user);

        when(xpService.calculateLevel(100L))
                .thenReturn(1);
    }


    @Test
    void createSubmission_ShouldCreateSubmissionSuccessfully() {

        stubValidSubmission();

        SubmissionRequestDTO request = validRequest();

        SubmissionResponseDTO result =
                submissionService.createSubmission(request, email);

        assertNotNull(result);

        verify(submissionRepository).save(any(Submission.class));

        verify(streakService).recordDailyActivity(1L);

        verify(dailyQuestService).recordChallengeCompletion(1L);

        verify(xpService).awardChallengeXp(1L, "EASY");

        verify(weeklyQuestService).recordChallengeCompletion(1L);

        verify(achievementService).unlockAchievement(
                1L,
                "FIRST_CHALLENGE",
                "First Challenge",
                "Complete your first challenge.",
                "🥇"
        );
    }


    @Test
    void createSubmission_ShouldThrowException_WhenRequestIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(null, email)
        );

        verifyNoInteractions(userRepository, challengeRepository);
    }

    @Test
    void createSubmission_ShouldThrowException_WhenChallengeIdIsNull() {

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, email)
        );

        verifyNoInteractions(userRepository, challengeRepository);
    }

    @Test
    void createSubmission_ShouldThrowException_WhenEmailIsNull() {

        SubmissionRequestDTO request = validRequest();

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, null)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void createSubmission_ShouldThrowException_WhenEmailIsBlank() {

        SubmissionRequestDTO request = validRequest();

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, " ")
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void createSubmission_ShouldThrowException_WhenUserNotFound() {

        SubmissionRequestDTO request = validRequest();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> submissionService.createSubmission(request, email)
        );

        verifyNoInteractions(challengeRepository);
    }

    @Test
    void createSubmission_ShouldThrowException_WhenChallengeNotFound() {

        SubmissionRequestDTO request = validRequest();

        stubUser();

        when(challengeRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> submissionService.createSubmission(request, email)
        );

        verifyNoInteractions(questionRepository);
    }


    @Test
    void createSubmission_ShouldThrowException_WhenChallengeHasNoQuestions() {

        stubUser();
        stubChallenge();

        when(questionRepository.findByChallengeId(10L))
                .thenReturn(List.of());

        assertThrows(
                IllegalStateException.class,
                () -> submissionService.createSubmission(
                        validRequest(), email
                )
        );

        verifyNoInteractions(optionRepository);
    }

    @Test
    void createSubmission_ShouldThrowException_WhenAnswersAreNull() {

        stubUser();
        stubChallenge();
        stubQuestions();

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(10L);
        when(request.getAnswers()).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, email)
        );

        verifyNoInteractions(optionRepository);
    }

    @Test
    void createSubmission_ShouldThrowException_WhenAnswerCountIsIncorrect() {

        stubUser();
        stubChallenge();
        stubQuestions();

        SubmissionAnswerDTO answer =
                mock(SubmissionAnswerDTO.class);

        when(answer.getQuestionId()).thenReturn(101L);
        when(answer.getOptionId()).thenReturn(201L);

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(10L);
        when(request.getAnswers()).thenReturn(List.of(answer));

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, email)
        );

        verifyNoInteractions(optionRepository);
    }

    @Test
    void createSubmission_ShouldRejectNullAnswer() {

        stubUser();
        stubChallenge();
        stubQuestions();

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(10L);

        when(request.getAnswers())
                .thenReturn(java.util.Arrays.asList(null, null));

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, email)
        );

        verifyNoInteractions(optionRepository);
    }


    @Test
    void createSubmission_ShouldRejectDuplicateQuestionAnswers() {

        stubUser();
        stubChallenge();
        stubQuestions();

        SubmissionAnswerDTO answer1 =
                mock(SubmissionAnswerDTO.class);

        SubmissionAnswerDTO answer2 =
                mock(SubmissionAnswerDTO.class);

        when(answer1.getQuestionId()).thenReturn(101L);
        when(answer1.getOptionId()).thenReturn(201L);

        when(answer2.getQuestionId()).thenReturn(101L);
        when(answer2.getOptionId()).thenReturn(202L);

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(10L);

        when(request.getAnswers())
                .thenReturn(List.of(answer1, answer2));

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, email)
        );

        verifyNoInteractions(optionRepository);
    }


    @Test
    void createSubmission_ShouldRejectQuestionFromAnotherChallenge() {

        stubUser();
        stubChallenge();
        stubQuestions();

        SubmissionAnswerDTO answer1 =
                mock(SubmissionAnswerDTO.class);

        SubmissionAnswerDTO answer2 =
                mock(SubmissionAnswerDTO.class);

        when(answer1.getQuestionId()).thenReturn(101L);
        when(answer1.getOptionId()).thenReturn(201L);

        when(answer2.getQuestionId()).thenReturn(999L);
        when(answer2.getOptionId()).thenReturn(202L);

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(10L);

        when(request.getAnswers())
                .thenReturn(List.of(answer1, answer2));

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, email)
        );

        verifyNoInteractions(optionRepository);
    }



    @Test
    void createSubmission_ShouldRejectNullQuestionId() {

        stubUser();
        stubChallenge();
        stubQuestions();

        SubmissionAnswerDTO answer1 =
                mock(SubmissionAnswerDTO.class);

        SubmissionAnswerDTO answer2 =
                mock(SubmissionAnswerDTO.class);

        when(answer1.getQuestionId()).thenReturn(null);
        when(answer1.getOptionId()).thenReturn(201L);

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(10L);

        when(request.getAnswers())
                .thenReturn(List.of(answer1, answer2));

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, email)
        );
    }

    @Test
    void createSubmission_ShouldRejectNullOptionId() {

        stubUser();
        stubChallenge();
        stubQuestions();

        SubmissionAnswerDTO answer1 =
                mock(SubmissionAnswerDTO.class);

        SubmissionAnswerDTO answer2 =
                mock(SubmissionAnswerDTO.class);

        when(answer1.getQuestionId()).thenReturn(101L);
        when(answer1.getOptionId()).thenReturn(null);

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(10L);

        when(request.getAnswers())
                .thenReturn(List.of(answer1, answer2));

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, email)
        );
    }

    @Test
    void createSubmission_ShouldThrowException_WhenOptionNotFound() {

        stubUser();
        stubChallenge();
        stubQuestions();

        SubmissionAnswerDTO answer1 =
                mock(SubmissionAnswerDTO.class);

        SubmissionAnswerDTO answer2 =
                mock(SubmissionAnswerDTO.class);

        when(answer1.getQuestionId()).thenReturn(101L);
        when(answer1.getOptionId()).thenReturn(201L);

        when(answer2.getQuestionId()).thenReturn(102L);
        when(answer2.getOptionId()).thenReturn(202L);

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(10L);

        when(request.getAnswers())
                .thenReturn(List.of(answer1, answer2));

        when(optionRepository.findById(201L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> submissionService.createSubmission(request, email)
        );
    }


    @Test
    void createSubmission_ShouldRejectOptionFromAnotherQuestion() {

        stubUser();
        stubChallenge();
        stubQuestions();

        SubmissionAnswerDTO answer1 =
                mock(SubmissionAnswerDTO.class);

        SubmissionAnswerDTO answer2 =
                mock(SubmissionAnswerDTO.class);

        when(answer1.getQuestionId()).thenReturn(101L);
        when(answer1.getOptionId()).thenReturn(201L);

        when(answer2.getQuestionId()).thenReturn(102L);
        when(answer2.getOptionId()).thenReturn(202L);

        SubmissionRequestDTO request =
                mock(SubmissionRequestDTO.class);

        when(request.getChallengeId()).thenReturn(10L);

        when(request.getAnswers())
                .thenReturn(List.of(answer1, answer2));

        Option option = mock(Option.class);

        when(optionRepository.findById(201L))
                .thenReturn(Optional.of(option));

        when(option.getQuestionId()).thenReturn(999L);

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.createSubmission(request, email)
        );
    }

    @Test
    void createSubmission_ShouldAwardXpForFirstSubmission() {

        stubValidSubmission();

        submissionService.createSubmission(validRequest(), email);

        verify(xpService).awardChallengeXp(1L, "EASY");

        verify(weeklyQuestService)
                .recordChallengeCompletion(1L);
    }


    @Test
    void createSubmission_ShouldNotAwardXpForRepeatSubmission() {

        stubValidSubmission();

        when(submissionRepository
                .findByUserIdAndChallengeIdOrderBySubmittedAtAsc(1L, 10L))
                .thenReturn(List.of(mock(Submission.class)));

        submissionService.createSubmission(validRequest(), email);

        verify(xpService, never())
                .awardChallengeXp(anyLong(), anyString());

        verify(weeklyQuestService, never())
                .recordChallengeCompletion(anyLong());

        verify(achievementService, never())
                .unlockAchievement(
                        anyLong(),
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString()
                );
    }


    @Test
    void createSubmission_ShouldUnlockPerfectScoreAchievement() {

        stubValidSubmission();

        Option option1 = mock(Option.class);
        Option option2 = mock(Option.class);

        when(optionRepository.findById(201L))
                .thenReturn(Optional.of(option1));

        when(optionRepository.findById(202L))
                .thenReturn(Optional.of(option2));

        when(option1.getQuestionId()).thenReturn(101L);
        when(option2.getQuestionId()).thenReturn(102L);

        when(option1.getIsCorrect()).thenReturn(true);
        when(option2.getIsCorrect()).thenReturn(true);

        when(option1.getOptionText()).thenReturn("A");
        when(option2.getOptionText()).thenReturn("B");

        submissionService.createSubmission(validRequest(), email);

        verify(achievementService).unlockAchievement(
                1L,
                "PERFECT_SCORE",
                "Perfect Score",
                "Score 100% on a challenge.",
                "💯"
        );
    }


    @Test
    void getMySubmissionById_ShouldReturnSubmission() {

        stubUser();

        Submission submission = mock(Submission.class);

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.of(submission));

        when(submission.getUserId()).thenReturn(1L);

        SubmissionResponseDTO result =
                submissionService.getMySubmissionById(1L, email);

        assertNotNull(result);

        verify(submissionRepository).findById(1L);
    }

    @Test
    void getMySubmissionById_ShouldThrowException_WhenSubmissionNotFound() {

        stubUser();

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> submissionService.getMySubmissionById(1L, email)
        );
    }


    @Test
    void getMySubmissionById_ShouldRejectUnauthorizedAccess() {

        stubUser();

        Submission submission = mock(Submission.class);

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.of(submission));

        when(submission.getUserId()).thenReturn(2L);

        assertThrows(
                IllegalStateException.class,
                () -> submissionService.getMySubmissionById(1L, email)
        );
    }

    @Test
    void getMySubmissions_ShouldReturnUserSubmissions() {

        stubUser();

        Submission submission = mock(Submission.class);

        when(submissionRepository.findByUserId(1L))
                .thenReturn(List.of(submission));

        List<SubmissionResponseDTO> result =
                submissionService.getMySubmissions(email);

        assertEquals(1, result.size());

        verify(submissionRepository).findByUserId(1L);
    }


    @Test
    void getMySubmissions_ShouldReturnEmptyList() {

        stubUser();

        when(submissionRepository.findByUserId(1L))
                .thenReturn(List.of());

        List<SubmissionResponseDTO> result =
                submissionService.getMySubmissions(email);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllSubmissions_ShouldReturnAllSubmissions() {

        Submission submission = mock(Submission.class);

        when(submissionRepository.findAll())
                .thenReturn(List.of(submission));

        List<SubmissionResponseDTO> result =
                submissionService.getAllSubmissions();

        assertEquals(1, result.size());
    }


    @Test
    void getAllSubmissions_ShouldReturnEmptyList() {

        when(submissionRepository.findAll())
                .thenReturn(List.of());

        List<SubmissionResponseDTO> result =
                submissionService.getAllSubmissions();

        assertTrue(result.isEmpty());
    }


    @Test
    void updateSubmissionStatus_ShouldUpdateToApproved() {

        Submission submission = mock(Submission.class);

        SubmissionStatusRequestDTO request =
                mock(SubmissionStatusRequestDTO.class);

        when(request.getStatus()).thenReturn(" approved ");

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.of(submission));

        when(submissionRepository.save(submission))
                .thenReturn(submission);

        SubmissionResponseDTO result =
                submissionService.updateSubmissionStatus(1L, request);

        assertNotNull(result);

        verify(submission).setStatus("APPROVED");

        verify(submissionRepository).save(submission);
    }


    @Test
    void updateSubmissionStatus_ShouldUpdateToRejected() {

        Submission submission = mock(Submission.class);

        SubmissionStatusRequestDTO request =
                mock(SubmissionStatusRequestDTO.class);

        when(request.getStatus()).thenReturn("REJECTED");

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.of(submission));

        when(submissionRepository.save(submission))
                .thenReturn(submission);

        submissionService.updateSubmissionStatus(1L, request);

        verify(submission).setStatus("REJECTED");
    }


    @Test
    void updateSubmissionStatus_ShouldRejectNullRequest() {

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.updateSubmissionStatus(1L, null)
        );

        verifyNoInteractions(submissionRepository);
    }


    @Test
    void updateSubmissionStatus_ShouldRejectNullStatus() {

        SubmissionStatusRequestDTO request =
                mock(SubmissionStatusRequestDTO.class);

        when(request.getStatus()).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.updateSubmissionStatus(1L, request)
        );

        verifyNoInteractions(submissionRepository);
    }


    @Test
    void updateSubmissionStatus_ShouldRejectBlankStatus() {

        SubmissionStatusRequestDTO request =
                mock(SubmissionStatusRequestDTO.class);

        when(request.getStatus()).thenReturn("   ");

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.updateSubmissionStatus(1L, request)
        );

        verifyNoInteractions(submissionRepository);
    }

    @Test
    void updateSubmissionStatus_ShouldRejectInvalidStatus() {

        SubmissionStatusRequestDTO request =
                mock(SubmissionStatusRequestDTO.class);

        when(request.getStatus()).thenReturn("PENDING");

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.of(mock(Submission.class)));

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.updateSubmissionStatus(1L, request)
        );

        verify(submissionRepository, never())
                .save(any(Submission.class));
    }


    @Test
    void updateSubmissionStatus_ShouldThrowException_WhenSubmissionNotFound() {

        SubmissionStatusRequestDTO request =
                mock(SubmissionStatusRequestDTO.class);

        when(request.getStatus()).thenReturn("APPROVED");

        when(submissionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> submissionService.updateSubmissionStatus(1L, request)
        );
    }
}
