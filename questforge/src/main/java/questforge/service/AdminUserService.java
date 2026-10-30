package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.AdminSubmissionResponseDTO;
import questforge.dto.AdminUserResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.model.User;
import questforge.model.UserStreak;
import questforge.repository.ChallengeRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;
import questforge.repository.UserStreakRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final UserStreakRepository userStreakRepository;
    private final ChallengeRepository challengeRepository;
    private final XPService xpService;

    public AdminUserService(
            UserRepository userRepository,
            SubmissionRepository submissionRepository,
            UserStreakRepository userStreakRepository,
            ChallengeRepository challengeRepository,
            XPService xpService) {

        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.userStreakRepository = userStreakRepository;
        this.challengeRepository = challengeRepository;
        this.xpService = xpService;
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    public List<AdminUserResponseDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        return buildUserResponses(users);
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================

    public AdminUserResponseDTO getUserById(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId
                ));

        return buildUserResponses(List.of(user)).get(0);
    }

    // =========================================================
    // SEARCH USERS
    // =========================================================

    public List<AdminUserResponseDTO> searchUsers(String query) {

        if (query == null || query.isBlank()) {

            return getAllUsers();
        }

        String normalizedQuery =
                query.trim().toLowerCase();

        List<User> users = userRepository.findAll()
                .stream()
                .filter(user -> {

                    String name = user.getName() == null
                            ? ""
                            : user.getName();

                    String email = user.getEmail() == null
                            ? ""
                            : user.getEmail();

                    return name.toLowerCase()
                            .contains(normalizedQuery)
                            || email.toLowerCase()
                            .contains(normalizedQuery);
                })
                .toList();

        return buildUserResponses(users);
    }

    // =========================================================
    // UPDATE USER ROLE
    // =========================================================

    @Transactional
    public AdminUserResponseDTO updateUserRole(
            Integer userId,
            String role) {

        if (role == null || role.isBlank()) {

            throw new IllegalArgumentException(
                    "Role is required"
            );
        }

        String normalizedRole =
                role.trim().toUpperCase();

        if (!normalizedRole.equals("USER")
                && !normalizedRole.equals("EMPLOYEE")
                && !normalizedRole.equals("ADMIN")) {

            throw new IllegalArgumentException(
                    "Invalid role. Allowed roles: USER, EMPLOYEE, ADMIN"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId
                ));

        user.setRole(normalizedRole);

        User savedUser =
                userRepository.save(user);

        return buildUserResponses(
                List.of(savedUser)
        ).get(0);
    }

    // =========================================================
    // ADD XP TO USER
    // =========================================================

    @Transactional
    public AdminUserResponseDTO addUserXp(
            Integer userId,
            int xpAmount) {

        if (xpAmount <= 0) {

            throw new IllegalArgumentException(
                    "XP amount must be greater than 0"
            );
        }

        User updatedUser =
                xpService.addXp(
                        userId,
                        xpAmount
                );

        return buildUserResponses(
                List.of(updatedUser)
        ).get(0);
    }

    // =========================================================
    // GET USER SUBMISSIONS
    // =========================================================

    public List<AdminSubmissionResponseDTO> getUserSubmissions(
            Integer userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId
                ));

        List<Submission> submissions =
                submissionRepository.findByUserId(userId);

        return convertSubmissionsToDTOs(submissions);
    }

    // =========================================================
    // BUILD USER RESPONSES
    // =========================================================

    private List<AdminUserResponseDTO> buildUserResponses(
            List<User> users) {

        List<Submission> submissions =
                submissionRepository.findAll();

        List<UserStreak> streaks =
                userStreakRepository.findAll();

        Map<Integer, UserStreak> streakMap =
                streaks.stream()
                        .collect(Collectors.toMap(
                                UserStreak::getUserId,
                                Function.identity()
                        ));

        return users.stream()
                .map(user ->
                        buildUserResponse(
                                user,
                                submissions,
                                streakMap
                        )
                )
                .toList();
    }

    // =========================================================
    // BUILD SINGLE USER RESPONSE
    // =========================================================

    private AdminUserResponseDTO buildUserResponse(
            User user,
            List<Submission> allSubmissions,
            Map<Integer, UserStreak> streakMap) {

        Integer userId = user.getId();

        int xp = user.getXp() == null
                ? 0
                : user.getXp();

        int level =
                xpService.calculateLevel(xp);

        List<Submission> userSubmissions =
                allSubmissions.stream()
                        .filter(submission ->
                                userId.equals(
                                        submission.getUserId()
                                ))
                        .toList();

        long challengesAttempted =
                userSubmissions.stream()
                        .filter(submission ->
                                Boolean.TRUE.equals(
                                        submission.getFirstSubmission()
                                ))
                        .count();

        long challengesCompleted =
                userSubmissions.stream()
                        .filter(submission ->
                                Boolean.TRUE.equals(
                                        submission.getFirstSubmission()
                                ))
                        .filter(submission ->
                                submission.getScore() != null
                        )
                        .count();

        UserStreak streak =
                streakMap.get(userId);

        int currentStreak = 0;
        int longestStreak = 0;

        if (streak != null) {

            currentStreak =
                    streak.getCurrentStreak();

            longestStreak =
                    streak.getLongestStreak();
        }

        return new AdminUserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                xp,
                level,
                challengesAttempted,
                challengesCompleted,
                currentStreak,
                longestStreak
        );
    }

    // =========================================================
    // CONVERT SUBMISSIONS TO DTOs
    // =========================================================

    private List<AdminSubmissionResponseDTO>
    convertSubmissionsToDTOs(
            List<Submission> submissions) {

        List<Integer> userIds =
                submissions.stream()
                        .map(Submission::getUserId)
                        .filter(userId -> userId != null)
                        .distinct()
                        .toList();

        List<Integer> challengeIds =
                submissions.stream()
                        .map(Submission::getChallengeId)
                        .filter(challengeId -> challengeId != null)
                        .distinct()
                        .toList();

        Map<Integer, User> users =
                userRepository.findAllById(userIds)
                        .stream()
                        .collect(Collectors.toMap(
                                User::getId,
                                Function.identity()
                        ));

        Map<Integer, Challenge> challenges =
                challengeRepository.findAllById(challengeIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Challenge::getId,
                                Function.identity()
                        ));

        return submissions.stream()
                .map(submission -> {

                    User user =
                            users.get(
                                    submission.getUserId()
                            );

                    Challenge challenge =
                            challenges.get(
                                    submission.getChallengeId()
                            );

                    String userName =
                            user != null
                                    ? user.getName()
                                    : "Unknown User";

                    String challengeTitle =
                            challenge != null
                                    ? challenge.getTitle()
                                    : "Deleted Challenge";

                    return new AdminSubmissionResponseDTO(
                            submission.getId(),
                            submission.getUserId(),
                            userName,
                            submission.getChallengeId(),
                            challengeTitle,
                            submission.getAnswer(),
                            submission.getStatus(),
                            submission.getSubmittedAt(),
                            submission.getTotalQuestions(),
                            submission.getCorrectAnswers(),
                            submission.getScore(),
                            submission.getFirstSubmission()
                    );
                })
                .toList();
    }
}