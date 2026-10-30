package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.AdminSubmissionResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.model.User;
import questforge.repository.ChallengeRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminSubmissionService {

    private final SubmissionRepository submissionRepository;

    private final UserRepository userRepository;

    private final ChallengeRepository challengeRepository;

    public AdminSubmissionService(
            SubmissionRepository submissionRepository,
            UserRepository userRepository,
            ChallengeRepository challengeRepository) {

        this.submissionRepository =
                submissionRepository;

        this.userRepository =
                userRepository;

        this.challengeRepository =
                challengeRepository;
    }

    public List<AdminSubmissionResponseDTO>
    getAllSubmissions() {

        List<Submission> submissions =
                submissionRepository.findAll();

        return convertToDTOs(submissions);
    }

    public List<AdminSubmissionResponseDTO>
    getSubmissionsByStatus(
            String status) {

        if (status == null ||
                status.isBlank()) {

            return getAllSubmissions();
        }

        String normalizedStatus =
                status.trim().toUpperCase();

        if (!normalizedStatus.equals("PENDING")
                && !normalizedStatus.equals("APPROVED")
                && !normalizedStatus.equals("REJECTED")) {

            throw new IllegalArgumentException(
                    "Invalid submission status: "
                            + status
            );
        }

        List<Submission> submissions =
                submissionRepository.findAll()
                        .stream()
                        .filter(submission ->
                                submission.getStatus() != null
                                        && normalizedStatus.equals(
                                        submission.getStatus()
                                                .trim()
                                                .toUpperCase()
                                )
                        )
                        .toList();

        return convertToDTOs(submissions);
    }

    public AdminSubmissionResponseDTO
    getSubmissionById(
            Integer submissionId) {

        Submission submission =
                submissionRepository
                        .findById(submissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Submission not found with id: "
                                                + submissionId
                                )
                        );

        return convertToDTOs(
                List.of(submission)
        ).get(0);
    }

    @Transactional
    public AdminSubmissionResponseDTO
    updateSubmissionStatus(
            Integer submissionId,
            String status) {

        if (status == null ||
                status.isBlank()) {

            throw new IllegalArgumentException(
                    "Submission status is required"
            );
        }

        String normalizedStatus =
                status.trim().toUpperCase();

        if (!normalizedStatus.equals("APPROVED")
                && !normalizedStatus.equals("REJECTED")) {

            throw new IllegalArgumentException(
                    "Invalid submission status: "
                            + status
                            + ". Status must be APPROVED or REJECTED."
            );
        }

        Submission submission =
                submissionRepository
                        .findById(submissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Submission not found with id: "
                                                + submissionId
                                )
                        );

        submission.setStatus(
                normalizedStatus
        );

        Submission savedSubmission =
                submissionRepository.save(
                        submission
                );

        return convertToDTOs(
                List.of(savedSubmission)
        ).get(0);
    }

    private List<AdminSubmissionResponseDTO>
    convertToDTOs(
            List<Submission> submissions) {

        List<Integer> userIds =
                submissions.stream()
                        .map(Submission::getUserId)
                        .filter(userId ->
                                userId != null
                        )
                        .distinct()
                        .toList();

        List<Integer> challengeIds =
                submissions.stream()
                        .map(Submission::getChallengeId)
                        .filter(challengeId ->
                                challengeId != null
                        )
                        .distinct()
                        .toList();

        Map<Integer, User> users =
                userRepository
                        .findAllById(userIds)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        User::getId,
                                        Function.identity()
                                )
                        );

        Map<Integer, Challenge> challenges =
                challengeRepository
                        .findAllById(challengeIds)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        Challenge::getId,
                                        Function.identity()
                                )
                        );

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