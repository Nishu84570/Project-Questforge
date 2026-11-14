
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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


    @Transactional(readOnly = true)
    public List<AdminUserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return buildUserResponses(users);
    }


    @Transactional(readOnly = true)
    public AdminUserResponseDTO getUserById(Long userId) {
        User user = findUserById(userId);

        return buildUserResponses(List.of(user)).get(0);
    }


    @Transactional(readOnly = true)
    public List<AdminUserResponseDTO> searchUsers(String query) {
        if (query == null || query.isBlank()) {
            return getAllUsers();
        }

        String normalizedQuery = query.trim();

        Map<Long, AdminUserResponseDTO> matchingUsers = new LinkedHashMap<>();

        userRepository
                .findByNameContainingIgnoreCase(normalizedQuery)
                .forEach(user -> matchingUsers.put(user.getUserId(), user));

        userRepository
                .findByNameContainingIgnoreCase(normalizedQuery)
                .forEach(user -> matchingUsers.put(user.getUserId(), user));

        List<AdminUserResponseDTO> users =
                new ArrayList<>(matchingUsers.values());

        users.sort(
                Comparator.comparing(
                        AdminUserResponseDTO::getName,
                        Comparator.nullsLast(
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
        );

        return users;
    }



    @Transactional
    public AdminUserResponseDTO updateUserRole(
            Long userId,
            String role) {

        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }

        String normalizedRole =
                role.trim().toUpperCase(Locale.ROOT);

        if (!normalizedRole.equals("USER")
                && !normalizedRole.equals("EMPLOYEE")
                && !normalizedRole.equals("ADMIN")) {

            throw new IllegalArgumentException(
                    "Invalid role. Allowed roles: USER, EMPLOYEE, ADMIN"
            );
        }

        User user = findUserById(userId);
        user.setRole(normalizedRole);

        User savedUser = userRepository.save(user);

        return buildUserResponses(List.of(savedUser)).get(0);
    }



    @Transactional
    public AdminUserResponseDTO addUserXp(
            Long userId,
            long xpAmount) {

        if (xpAmount <= 0L) {
            throw new IllegalArgumentException(
                    "XP amount must be greater than 0"
            );
        }

        User updatedUser = xpService.addXp(userId, xpAmount);

        return buildUserResponses(List.of(updatedUser)).get(0);
    }


    @Transactional(readOnly = true)
    public List<AdminSubmissionResponseDTO> getUserSubmissions(
            Long userId) {

        findUserById(userId);

        List<Submission> submissions =
                submissionRepository.findByUserId(userId);

        return convertSubmissionsToDTOs(submissions);
    }


    private List<AdminUserResponseDTO> buildUserResponses(
            List<User> users) {

        if (users.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = users.stream()
                .map(User::getId)
                .toList();

        Map<Long, List<Submission>> submissionsByUserId =
                submissionRepository.findAll()
                        .stream()
                        .filter(submission ->
                                userIds.contains(submission.getUserId())
                        )
                        .collect(Collectors.groupingBy(
                                Submission::getUserId
                        ));

        Map<Long, UserStreak> streakMap =
                userStreakRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                UserStreak::getUserId,
                                Function.identity(),
                                (first, second) -> first
                        ));

        return users.stream()
                .map(user -> buildUserResponse(
                        user,
                        submissionsByUserId.getOrDefault(
                                user.getId(),
                                List.of()
                        ),
                        streakMap.get(user.getId())
                ))
                .toList();
    }

    private AdminUserResponseDTO buildUserResponse(
            User user,
            List<Submission> userSubmissions,
            UserStreak streak) {

        long xp = user.getXp();
        int level = xpService.calculateLevel(xp);

        long challengesAttempted = userSubmissions.stream()
                .filter(submission ->
                        Boolean.TRUE.equals(
                                submission.getFirstSubmission()
                        )
                )
                .map(Submission::getChallengeId)
                .distinct()
                .count();

        long challengesCompleted = userSubmissions.stream()
                .filter(submission ->
                        Boolean.TRUE.equals(
                                submission.getFirstSubmission()
                        )
                )
                .filter(submission ->
                        submission.getScore() >= 100L
                )
                .map(Submission::getChallengeId)
                .distinct()
                .count();

        long currentStreak = streak == null
                ? 0L
                : streak.getCurrentStreak();

        long longestStreak = streak == null
                ? 0L
                : streak.getLongestStreak();

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


    private List<AdminSubmissionResponseDTO> convertSubmissionsToDTOs(
            List<Submission> submissions) {

        if (submissions.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = submissions.stream()
                .map(Submission::getUserId)
                .distinct()
                .toList();

        List<Long> challengeIds = submissions.stream()
                .map(Submission::getChallengeId)
                .distinct()
                .toList();

        Map<Long, User> users =
                userRepository.findAllById(userIds)
                        .stream()
                        .collect(Collectors.toMap(
                                User::getId,
                                Function.identity()
                        ));

        Map<Long, Challenge> challenges =
                challengeRepository.findAllById(challengeIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Challenge::getId,
                                Function.identity()
                        ));

        return submissions.stream()
                .map(submission -> {
                    User user = users.get(submission.getUserId());

                    Challenge challenge =
                            challenges.get(submission.getChallengeId());

                    String userName = user != null
                            ? user.getName()
                            : "Unknown User";

                    String challengeTitle = challenge != null
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


    private User findUserById(Long userId) {
        if (userId == null || userId <= 0L) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );
    }
}
