package questforge.service;

import org.springframework.stereotype.Service;
import questforge.dto.AttemptRequestDTO;
import questforge.dto.AttemptResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Answer;
import questforge.model.Attempt;
import questforge.model.Option;
import questforge.repository.AnswerRepository;
import questforge.repository.AttemptRepository;
import questforge.repository.ChallengeRepository;
import questforge.repository.OptionRepository;
import questforge.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final AnswerRepository answerRepository;
    private final OptionRepository optionRepository;

    public AttemptService(
            AttemptRepository attemptRepository,
            UserRepository userRepository,
            ChallengeRepository challengeRepository,
            AnswerRepository answerRepository,
            OptionRepository optionRepository) {

        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.answerRepository = answerRepository;
        this.optionRepository = optionRepository;
    }

    // CREATE ATTEMPT
    public AttemptResponseDTO createAttempt(AttemptRequestDTO request) {

        if (!userRepository.existsById(request.getUserId())) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + request.getUserId()
            );
        }

        if (!challengeRepository.existsById(request.getChallengeId())) {
            throw new ResourceNotFoundException(
                    "Challenge not found with id: " + request.getChallengeId()
            );
        }

        Attempt attempt = new Attempt();

        attempt.setUserId(request.getUserId());
        attempt.setChallengeId(request.getChallengeId());
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setScore(0);

        Attempt savedAttempt = attemptRepository.save(attempt);

        return convertToResponseDTO(savedAttempt);
    }

    // GET ALL
    public List<AttemptResponseDTO> getAllAttempts() {

        return attemptRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // GET BY ID
    public AttemptResponseDTO getAttemptById(Integer id) {

        Attempt attempt = attemptRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attempt not found with id: " + id
                        )
                );

        return convertToResponseDTO(attempt);
    }

    // GET ATTEMPTS BY USER
    public List<AttemptResponseDTO> getAttemptsByUser(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        return attemptRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // GET ATTEMPTS BY CHALLENGE
    public List<AttemptResponseDTO> getAttemptsByChallenge(
            Integer challengeId) {

        if (!challengeRepository.existsById(challengeId)) {
            throw new ResourceNotFoundException(
                    "Challenge not found with id: " + challengeId
            );
        }

        return attemptRepository.findByChallengeId(challengeId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // COMPLETE ATTEMPT + AUTOMATIC SCORE
    public AttemptResponseDTO completeAttempt(Integer id) {

        Attempt attempt = attemptRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attempt not found with id: " + id
                        )
                );

        // Prevent completing an already completed attempt
        if (attempt.getCompletedAt() != null) {
            throw new IllegalStateException(
                    "Attempt is already completed"
            );
        }

        List<Answer> answers =
                answerRepository.findByAttemptId(id);

        int score = 0;

        for (Answer answer : answers) {

            Option option = optionRepository.findById(
                    answer.getOptionId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Option not found with id: "
                                    + answer.getOptionId()
                    )
            );

            if (Boolean.TRUE.equals(option.getIsCorrect())) {
                score++;
            }
        }

        attempt.setScore(score);
        attempt.setCompletedAt(LocalDateTime.now());

        Attempt completedAttempt =
                attemptRepository.save(attempt);

        return convertToResponseDTO(completedAttempt);
    }

    // DELETE
    public void deleteAttempt(Integer id) {

        Attempt attempt = attemptRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attempt not found with id: " + id
                        )
                );

        attemptRepository.delete(attempt);
    }

    // ENTITY → RESPONSE DTO
    private AttemptResponseDTO convertToResponseDTO(
            Attempt attempt) {

        return new AttemptResponseDTO(
                attempt.getId(),
                attempt.getUserId(),
                attempt.getChallengeId(),
                attempt.getStartedAt(),
                attempt.getCompletedAt(),
                attempt.getScore()
        );
    }
}