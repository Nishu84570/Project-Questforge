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
import java.util.Set;

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

    // ==========================================
    // CREATE SUBMISSION
    // ==========================================

    @Transactional
    public SubmissionResponseDTO createSubmission(
            SubmissionRequestDTO request,
            String email) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Challenge challenge =
                challengeRepository
                        .findById(
                                request.getChallengeId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Challenge not found with id: "
                                                + request.getChallengeId()
                                )
                        );

        List<Question> questions =
                questionRepository
                        .findByChallengeId(
                                challenge.getId()
                        );

        if (questions.isEmpty()) {

            throw new IllegalStateException(
                    "This challenge has no questions"
            );
        }

        if (request.getAnswers() == null
                || request.getAnswers().size()
                != questions.size()) {

            throw new IllegalStateException(
                    "Please answer all questions"
            );
        }

        Set<Integer> validQuestionIds =
                new HashSet<>();

        for (Question question :
                questions) {

            validQuestionIds.add(
                    question.getId()
            );
        }

        Set<Integer> submittedQuestionIds =
                new HashSet<>();

        for (SubmissionAnswerDTO answer :
                request.getAnswers()) {

            if (!submittedQuestionIds.add(
                    answer.getQuestionId()
            )) {

                throw new IllegalStateException(
                        "Duplicate answer for question: "
                                + answer.getQuestionId()
                );
            }
        }

        if (!submittedQuestionIds.equals(
                validQuestionIds)) {

            throw new IllegalStateException(
                    "Answers do not match challenge questions"
            );
        }

        int correctAnswers = 0;

        StringBuilder answerText =
                new StringBuilder();

        for (SubmissionAnswerDTO submittedAnswer :
                request.getAnswers()) {

            Question question =
                    questionRepository
                            .findById(
                                    submittedAnswer
                                            .getQuestionId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Question not found with id: "
                                                    + submittedAnswer
                                                    .getQuestionId()
                                    )
                            );

            if (!question.getChallengeId()
                    .equals(challenge.getId())) {

                throw new IllegalStateException(
                        "Question does not belong to this challenge"
                );
            }

            Option option =
                    optionRepository
                            .findById(
                                    submittedAnswer
                                            .getOptionId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Option not found with id: "
                                                    + submittedAnswer
                                                    .getOptionId()
                                    )
                            );

            if (!option.getQuestionId()
                    .equals(question.getId())) {

                throw new IllegalStateException(
                        "Option does not belong to the selected question"
                );
            }

            if (Boolean.TRUE.equals(
                    option.getIsCorrect())) {

                correctAnswers++;
            }

            if (answerText.length() > 0) {
                answerText.append(" | ");
            }

            answerText
                    .append("Q")
                    .append(question.getId())
                    .append(": ")
                    .append(option.getOptionText());
        }

        int totalQuestions =
                questions.size();

        int score =
                (correctAnswers * 100)
                        / totalQuestions;

        List<Submission> previousSubmissions =
                submissionRepository
                        .findByUserIdAndChallengeIdOrderBySubmittedAtAsc(
                                user.getId(),
                                challenge.getId()
                        );

        boolean firstSubmission =
                previousSubmissions.isEmpty();

        Submission submission =
                new Submission();

        submission.setUserId(
                user.getId()
        );

        submission.setChallengeId(
                challenge.getId()
        );

        submission.setAnswer(
                answerText.toString()
        );

        submission.setStatus(
                "PENDING"
        );

        submission.setSubmittedAt(
                LocalDateTime.now()
        );

        submission.setTotalQuestions(
                totalQuestions
        );

        submission.setCorrectAnswers(
                correctAnswers
        );

        submission.setScore(
                score
        );

        submission.setFirstSubmission(
                firstSubmission
        );

        Submission savedSubmission =
                submissionRepository.save(
                        submission
                );

        // ==========================================
        // DAILY STREAK
        // ==========================================

        streakService.recordDailyActivity(
                user.getId()
        );

        // ==========================================
        // DAILY QUEST
        // ==========================================

        /*
         * Daily Quest is based on completing
         * a challenge today.
         *
         * The DailyQuestService itself prevents
         * duplicate daily rewards.
         */
        dailyQuestService.recordChallengeCompletion(
                user.getId()
        );

        // ==========================================
        // FIRST SUBMISSION REWARDS
        // ==========================================

        if (firstSubmission) {

            // --------------------------------------
            // CHALLENGE XP
            // --------------------------------------

            user =
                    xpService.awardChallengeXp(
                            user.getId(),
                            challenge.getDifficulty()
                    );

            // --------------------------------------
            // WEEKLY QUEST
            // --------------------------------------

            /*
             * Only a first submission for a challenge
             * counts toward the weekly quest.
             *
             * Repeating the same challenge will not
             * increase weekly quest progress.
             */
            weeklyQuestService
                    .recordChallengeCompletion(
                            user.getId()
                    );

            // --------------------------------------
            // ACHIEVEMENTS
            // --------------------------------------

            checkAchievements(
                    user,
                    savedSubmission
            );
        }

        return convertToResponseDTO(
                savedSubmission
        );
    }

    // ==========================================
    // ACHIEVEMENTS
    // ==========================================

    private void checkAchievements(
            User user,
            Submission submission) {

        Integer userId =
                user.getId();

        achievementService.unlockAchievement(
                userId,
                "FIRST_CHALLENGE",
                "First Challenge",
                "Complete your first challenge.",
                "🥇"
        );

        if (submission.getScore() != null
                && submission.getScore() == 100) {

            achievementService.unlockAchievement(
                    userId,
                    "PERFECT_SCORE",
                    "Perfect Score",
                    "Score 100% on a challenge.",
                    "💯"
            );
        }

        int currentXp =
                user.getXp() == null
                        ? 0
                        : user.getXp();

        if (currentXp >= 500) {

            achievementService.unlockAchievement(
                    userId,
                    "XP_HUNTER",
                    "XP Hunter",
                    "Reach 500 XP.",
                    "⚡"
            );
        }

        List<Submission> firstSubmissions =
                submissionRepository
                        .findByUserId(userId)
                        .stream()
                        .filter(
                                item ->
                                        Boolean.TRUE.equals(
                                                item.getFirstSubmission()
                                        )
                        )
                        .toList();

        Set<Integer> uniqueChallengeIds =
                new HashSet<>();

        for (Submission item :
                firstSubmissions) {

            uniqueChallengeIds.add(
                    item.getChallengeId()
            );
        }

        if (uniqueChallengeIds.size() >= 10) {

            achievementService.unlockAchievement(
                    userId,
                    "CHALLENGE_MASTER",
                    "Challenge Master",
                    "Complete 10 different challenges.",
                    "🎯"
            );
        }

        int level =
                calculateLevel(currentXp);

        if (level >= 10) {

            achievementService.unlockAchievement(
                    userId,
                    "LEVEL_10",
                    "Level 10",
                    "Reach level 10.",
                    "👑"
            );
        }
    }

    // ==========================================
    // LEVEL CALCULATION
    // ==========================================

    private int calculateLevel(
            Integer xp) {

        if (xp == null || xp < 100) {
            return 1;
        }

        if (xp < 250) {
            return 2;
        }

        if (xp < 450) {
            return 3;
        }

        if (xp < 700) {
            return 4;
        }

        if (xp < 1000) {
            return 5;
        }

        return 6 + ((xp - 1000) / 500);
    }

    // ==========================================
    // GET MY SUBMISSION BY ID
    // ==========================================

    public SubmissionResponseDTO getMySubmissionById(
            Integer submissionId,
            String email) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Submission submission =
                submissionRepository
                        .findById(submissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Submission not found with id: "
                                                + submissionId
                                )
                        );

        if (!submission.getUserId()
                .equals(user.getId())) {

            throw new IllegalStateException(
                    "You are not allowed to view this submission"
            );
        }

        return convertToResponseDTO(
                submission
        );
    }

    // ==========================================
    // GET MY SUBMISSIONS
    // ==========================================

    public List<SubmissionResponseDTO>
    getMySubmissions(String email) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        return submissionRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // ==========================================
    // GET ALL SUBMISSIONS
    // ==========================================

    public List<SubmissionResponseDTO>
    getAllSubmissions() {

        return submissionRepository
                .findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // ==========================================
    // UPDATE SUBMISSION STATUS
    // ==========================================

    public SubmissionResponseDTO
    updateSubmissionStatus(
            Integer id,
            SubmissionStatusRequestDTO request) {

        Submission submission =
                submissionRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Submission not found with id: "
                                                + id
                                )
                        );

        String status =
                request.getStatus()
                        .toUpperCase();

        if (!status.equals("APPROVED")
                && !status.equals("REJECTED")) {

            throw new IllegalStateException(
                    "Status must be APPROVED or REJECTED"
            );
        }

        submission.setStatus(status);

        Submission updatedSubmission =
                submissionRepository.save(
                        submission
                );

        return convertToResponseDTO(
                updatedSubmission
        );
    }

    // ==========================================
    // CONVERT ENTITY → RESPONSE DTO
    // ==========================================

    private SubmissionResponseDTO
    convertToResponseDTO(
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
                submission.getFirstSubmission()
        );
    }
}