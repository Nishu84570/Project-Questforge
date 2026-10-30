
package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final AchievementService achievementService;
    private final StreakService streakService;
    private final DailyQuestService dailyQuestService;
    private final XPService xpService;
    private final WeeklyQuestService weeklyQuestService;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            UserRepository userRepository,
            ChallengeRepository challengeRepository,
            QuestionRepository questionRepository,
            OptionRepository optionRepository,
            AchievementService achievementService,
            StreakService streakService,
            DailyQuestService dailyQuestService,
            XPService xpService,
            WeeklyQuestService weeklyQuestService) {

        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.achievementService = achievementService;
        this.streakService = streakService;
        this.dailyQuestService = dailyQuestService;
        this.xpService = xpService;
        this.weeklyQuestService = weeklyQuestService;
    }


    @Transactional
    public SubmissionResponseDTO createSubmission(
            SubmissionRequestDTO request,
            String email) {

        if (request == null || request.getChallengeId() == null) {
            throw new IllegalArgumentException(
                    "Submission request and challenge ID are required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated user email is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Challenge challenge = challengeRepository
                .findById(request.getChallengeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Challenge not found with id: "
                                        + request.getChallengeId()));

        List<Question> questions =
                questionRepository.findByChallengeId(challenge.getId());

        if (questions.isEmpty()) {
            throw new IllegalStateException(
                    "This challenge has no questions");
        }

        List<SubmissionAnswerDTO> answers = request.getAnswers();

        if (answers == null || answers.size() != questions.size()) {
            throw new IllegalArgumentException(
                    "Please answer all questions exactly once");
        }

        Map<Long, Question> questionsById = questions.stream()
                .collect(Collectors.toMap(
                        Question::getId,
                        Function.identity()));

        Set<Long> submittedQuestionIds = new HashSet<>();

        for (SubmissionAnswerDTO answer : answers) {
            if (answer == null
                    || answer.getQuestionId() == null
                    || answer.getOptionId() == null) {
                throw new IllegalArgumentException(
                        "Every answer must contain a question ID and option ID");
            }

            if (!submittedQuestionIds.add(answer.getQuestionId())) {
                throw new IllegalArgumentException(
                        "Duplicate answer for question: "
                                + answer.getQuestionId());
            }

            if (!questionsById.containsKey(answer.getQuestionId())) {
                throw new IllegalArgumentException(
                        "Question does not belong to this challenge: "
                                + answer.getQuestionId());
            }
        }

        if (!submittedQuestionIds.equals(questionsById.keySet())) {
            throw new IllegalArgumentException(
                    "Answers do not match the challenge questions");
        }

        long correctAnswers = 0L;
        StringBuilder answerText = new StringBuilder();

        for (SubmissionAnswerDTO submittedAnswer : answers) {
            Question question =
                    questionsById.get(submittedAnswer.getQuestionId());

            Option option = optionRepository
                    .findById(submittedAnswer.getOptionId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Option not found with id: "
                                            + submittedAnswer.getOptionId()));

            if (!question.getId().equals(option.getQuestionId())) {
                throw new IllegalArgumentException(
                        "Option does not belong to the selected question");
            }

            if (Boolean.TRUE.equals(option.getIsCorrect())) {
                correctAnswers++;
            }

            if (!answerText.isEmpty()) {
                answerText.append(" | ");
            }

            answerText.append("Q")
                    .append(question.getId())
                    .append(": ")
                    .append(option.getOptionText());
        }

        long totalQuestions = questions.size();
        long score = (correctAnswers * 100L) / totalQuestions;

        boolean firstSubmission =
                !submissionRepository
                        .findByUserIdAndChallengeIdOrderBySubmittedAtAsc(
                                user.getId(),
                                challenge.getId())
                        .isEmpty();

        firstSubmission = !firstSubmission;

        Submission submission = new Submission();
        submission.setUserId(user.getId());
        submission.setChallengeId(challenge.getId());
        submission.setAnswer(answerText.toString());
        submission.setStatus("PENDING");
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setTotalQuestions(totalQuestions);
        submission.setCorrectAnswers(correctAnswers);
        submission.setScore(score);
        submission.setFirstSubmission(firstSubmission);

        Submission savedSubmission = submissionRepository.save(submission);

        streakService.recordDailyActivity(user.getId());
        dailyQuestService.recordChallengeCompletion(user.getId());

        if (firstSubmission) {
            user = xpService.awardChallengeXp(
                    user.getId(),
                    challenge.getDifficulty());

            // Count this challenge toward the weekly quest once.
            weeklyQuestService.recordChallengeCompletion(user.getId());

            checkAchievements(user, savedSubmission);
        }

        return convertToResponseDTO(savedSubmission);
    }


    private void checkAchievements(
            User user,
            Submission submission) {

        Long userId = user.getId();

        achievementService.unlockAchievement(
                userId,
                "FIRST_CHALLENGE",
                "First Challenge",
                "Complete your first challenge.",
                "🥇");

        if (submission.getScore() >= 100L) {
            achievementService.unlockAchievement(
                    userId,
                    "PERFECT_SCORE",
                    "Perfect Score",
                    "Score 100% on a challenge.",
                    "💯");
        }

        long currentXp = user.getXp();

        if (currentXp >= 500L) {
            achievementService.unlockAchievement(
                    userId,
                    "XP_HUNTER",
                    "XP Hunter",
                    "Reach 500 XP.",
                    "⚡");
        }

        long uniqueChallenges = submissionRepository
                .findByUserId(userId)
                .stream()
                .filter(item ->
                        Boolean.TRUE.equals(item.getFirstSubmission()))
                .map(Submission::getChallengeId)
                .distinct()
                .count();

        if (uniqueChallenges >= 10L) {
            achievementService.unlockAchievement(
                    userId,
                    "CHALLENGE_MASTER",
                    "Challenge Master",
                    "Submit 10 different challenges for the first time.",
                    "🎯");
        }

        int level = xpService.calculateLevel(currentXp);

        if (level >= 10) {
            achievementService.unlockAchievement(
                    userId,
                    "LEVEL_10",
                    "Level 10",
                    "Reach level 10.",
                    "👑");
        }
    }


    @Transactional(readOnly = true)
    public SubmissionResponseDTO getMySubmissionById(
            Long submissionId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Submission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Submission not found with id: "
                                        + submissionId));

        if (!submission.getUserId().equals(user.getId())) {
            throw new IllegalStateException(
                    "You are not allowed to view this submission");
        }

        return convertToResponseDTO(submission);
    }



    @Transactional(readOnly = true)
    public List<SubmissionResponseDTO> getMySubmissions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return submissionRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<SubmissionResponseDTO> getAllSubmissions() {

        return submissionRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }


    @Transactional
    public SubmissionResponseDTO updateSubmissionStatus(
            Long id,
            SubmissionStatusRequestDTO request) {

        if (request == null
                || request.getStatus() == null
                || request.getStatus().isBlank()) {
            throw new IllegalArgumentException(
                    "Submission status is required");
        }

        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Submission not found with id: " + id));

        String status = request.getStatus()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!status.equals("APPROVED")
                && !status.equals("REJECTED")) {
            throw new IllegalArgumentException(
                    "Status must be APPROVED or REJECTED");
        }

        submission.setStatus(status);

        return convertToResponseDTO(
                submissionRepository.save(submission));
    }


    private SubmissionResponseDTO convertToResponseDTO(
            Submission submission) {

        return new SubmissionResponseDTO(
                submission.getId(),
                submission.getUserId(),
                submission.getChallengeId(),
                submission.getAnswer(),
                submission.getStatus(),
                submission.getSubmittedAt(),
                submission.getTotalQuestions(),
                submission.getCorrectAnswers(),
                submission.getScore(),
                submission.getFirstSubmission());
    }
}
