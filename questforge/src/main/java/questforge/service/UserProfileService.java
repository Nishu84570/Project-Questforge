package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import questforge.dto.ProfileUpdateRequestDTO;
import questforge.dto.UserProfileDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Submission;
import questforge.model.User;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;

    public UserProfileService(
            UserRepository userRepository,
            SubmissionRepository submissionRepository) {

        this.userRepository =
                userRepository;

        this.submissionRepository =
                submissionRepository;
    }

    // ==========================================
    // GET MY PROFILE
    // ==========================================

    public UserProfileDTO getMyProfile(
            String email) {

        User user =
                findUserByEmail(email);

        List<Submission> submissions =
                submissionRepository
                        .findByUserId(user.getId());

        int totalAttempts =
                submissions.size();

        int firstAttempts =
                (int) submissions.stream()
                        .filter(submission ->
                                Boolean.TRUE.equals(
                                        submission.getFirstSubmission()
                                )
                        )
                        .count();

        int practiceAttempts =
                totalAttempts - firstAttempts;

        int averageScore = 0;
        int bestScore = 0;

        if (!submissions.isEmpty()) {

            int totalScore =
                    submissions.stream()
                            .mapToInt(submission ->
                                    submission.getScore() != null
                                            ? submission.getScore()
                                            : 0
                            )
                            .sum();

            averageScore =
                    Math.round(
                            (float) totalScore
                                    / totalAttempts
                    );

            bestScore =
                    submissions.stream()
                            .mapToInt(submission ->
                                    submission.getScore() != null
                                            ? submission.getScore()
                                            : 0
                            )
                            .max()
                            .orElse(0);
        }

        int totalCorrectAnswers =
                submissions.stream()
                        .mapToInt(submission ->
                                submission.getCorrectAnswers() != null
                                        ? submission.getCorrectAnswers()
                                        : 0
                        )
                        .sum();

        int totalQuestionsAttempted =
                submissions.stream()
                        .mapToInt(submission ->
                                submission.getTotalQuestions() != null
                                        ? submission.getTotalQuestions()
                                        : 0
                        )
                        .sum();

        boolean profileImagePresent =
                user.getProfileImage() != null
                        && user.getProfileImage().length > 0;

        String profileImageUrl =
                profileImagePresent
                        ? "/profile/avatar"
                        : null;

        return new UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),

                totalAttempts,
                firstAttempts,
                practiceAttempts,

                averageScore,
                bestScore,

                totalCorrectAnswers,
                totalQuestionsAttempted,

                profileImagePresent,
                profileImageUrl
        );
    }

    // ==========================================
    // UPDATE MY PROFILE
    // ==========================================

    public UserProfileDTO updateMyProfile(
            String email,
            ProfileUpdateRequestDTO request) {

        User user =
                findUserByEmail(email);

        String newName =
                request.getName() == null
                        ? ""
                        : request.getName().trim();

        if (newName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Name cannot be empty"
            );
        }

        if (newName.length() > 100) {
            throw new IllegalArgumentException(
                    "Name cannot exceed 100 characters"
            );
        }

        user.setName(newName);

        userRepository.save(user);

        return getMyProfile(email);
    }

    // ==========================================
    // UPLOAD PROFILE IMAGE
    // ==========================================

    public UserProfileDTO uploadProfileImage(
            String email,
            MultipartFile file)
            throws IOException {

        User user =
                findUserByEmail(email);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select an image"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null
                || !contentType.startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed"
            );
        }

        long maxSize =
                5L * 1024 * 1024;

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    "Profile image must be smaller than 5 MB"
            );
        }

        user.setProfileImage(
                file.getBytes()
        );

        user.setProfileImageContentType(
                contentType
        );

        userRepository.save(user);

        return getMyProfile(email);
    }

    // ==========================================
    // GET PROFILE IMAGE
    // ==========================================

    public User getUserWithProfileImage(
            String email) {

        return findUserByEmail(email);
    }

    // ==========================================
    // DELETE PROFILE IMAGE
    // ==========================================

    public UserProfileDTO deleteProfileImage(
            String email) {

        User user =
                findUserByEmail(email);

        user.setProfileImage(null);
        user.setProfileImageContentType(null);

        userRepository.save(user);

        return getMyProfile(email);
    }

    // ==========================================
    // FIND USER
    // ==========================================

    private User findUserByEmail(
            String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }
}