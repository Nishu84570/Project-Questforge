package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Set;

@Service
public class UserProfileService {

    private static final long MAX_IMAGE_SIZE = 5L * 1024L * 1024L;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;

    public UserProfileService(
            UserRepository userRepository,
            SubmissionRepository submissionRepository) {

        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
    }

    // GET MY PROFILE

    @Transactional(readOnly = true)
    public UserProfileDTO getMyProfile(String email) {
        User user = findUserByEmail(email);

        List<Submission> submissions =
                submissionRepository.findByUserId(user.getId());

        long totalAttempts = submissions.size();

        long firstAttempts = submissions.stream()
                .filter(submission ->
                        Boolean.TRUE.equals(
                                submission.getFirstSubmission()
                        )
                )
                .count();

        long practiceAttempts = totalAttempts - firstAttempts;

        long totalScore = submissions.stream()
                .mapToLong(Submission::getScore)
                .sum();

        long averageScore = totalAttempts == 0L
                ? 0L
                : Math.round((double) totalScore / totalAttempts);

        long bestScore = submissions.stream()
                .mapToLong(Submission::getScore)
                .max()
                .orElse(0L);

        long totalCorrectAnswers = submissions.stream()
                .mapToLong(Submission::getCorrectAnswers)
                .sum();

        long totalQuestionsAttempted = submissions.stream()
                .mapToLong(Submission::getTotalQuestions)
                .sum();

        byte[] profileImage = user.getProfileImage();

        boolean profileImagePresent =
                profileImage != null && profileImage.length > 0;

        String profileImageUrl = profileImagePresent
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



    @Transactional
    public UserProfileDTO updateMyProfile(
            String email,
            ProfileUpdateRequestDTO request) {

        if (request == null || request.getName() == null) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        String newName = request.getName().trim();

        if (newName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (newName.length() > 100) {
            throw new IllegalArgumentException(
                    "Name cannot exceed 100 characters"
            );
        }

        User user = findUserByEmail(email);
        user.setName(newName);
        userRepository.save(user);

        return getMyProfile(email);
    }



    @Transactional
    public UserProfileDTO uploadProfileImage(
            String email,
            MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select an image"
            );
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Profile image must not exceed 5 MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_IMAGE_TYPES.contains(
                        contentType.toLowerCase()
                )) {

            throw new IllegalArgumentException(
                    "Only JPEG, PNG and WebP images are allowed"
            );
        }

        User user = findUserByEmail(email);

        user.setProfileImage(file.getBytes());
        user.setProfileImageContentType(contentType.toLowerCase());

        userRepository.save(user);

        return getMyProfile(email);
    }



    @Transactional(readOnly = true)
    public User getUserWithProfileImage(String email) {
        return findUserByEmail(email);
    }



    @Transactional
    public UserProfileDTO deleteProfileImage(String email) {
        User user = findUserByEmail(email);

        user.setProfileImage(null);
        user.setProfileImageContentType(null);

        userRepository.save(user);

        return getMyProfile(email);
    }


    private User findUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email cannot be empty"
            );
        }

        return userRepository.findByEmail(email.trim())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }
}