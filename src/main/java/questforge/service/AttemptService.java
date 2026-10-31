
package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final AnswerRepository answerRepository;
    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;

    public AttemptService(
            AttemptRepository attemptRepository,
            UserRepository userRepository,
            ChallengeRepository challengeRepository,
            AnswerRepository answerRepository,
            OptionRepository optionRepository,
            QuestionRepository questionRepository) {

        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.answerRepository = answerRepository;
        this.optionRepository = optionRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public AttemptResponseDTO createAttempt(AttemptRequestDTO request) {

        if (!userRepository.existsById(request.getUserId())) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + request.getUserId());
        }

        if (!challengeRepository.existsById(request.getChallengeId())) {
            throw new ResourceNotFoundException(
                    "Challenge not found with id: " + request.getChallengeId());
        }

        Attempt attempt = new Attempt();
        attempt.setUserId(request.getUserId());
        attempt.setChallengeId(request.getChallengeId());
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setScore(0L);

        return convertToResponseDTO(attemptRepository.save(attempt));
    }

    @Transactional(readOnly = true)
    public List<AttemptResponseDTO> getAllAttempts() {
        return attemptRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public AttemptResponseDTO getAttemptById(Long id) {

        Attempt attempt = findAttempt(id);
        return convertToResponseDTO(attempt);
    }

    @Transactional(readOnly = true)
    public List<AttemptResponseDTO> getAttemptsByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId);
        }

        return attemptRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttemptResponseDTO> getAttemptsByChallenge(
            Long challengeId) {

        if (!challengeRepository.existsById(challengeId)) {
            throw new ResourceNotFoundException(
                    "Challenge not found with id: " + challengeId);
        }

        return attemptRepository.findByChallengeId(challengeId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional
    public AttemptResponseDTO completeAttempt(Long id) {

        Attempt attempt = findAttempt(id);

        if (attempt.getCompletedAt() != null) {
            throw new IllegalStateException(
                    "Attempt is already completed");
        }

        List<Answer> answers = answerRepository.findByAttemptId(id);

        long score = 0L;

        for (Answer answer : answers) {

            Question question = questionRepository
                    .findById(answer.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Question not found with id: "
                                    + answer.getQuestionId()));

            if (!question.getChallengeId().equals(
                    attempt.getChallengeId())) {
                throw new IllegalStateException(
                        "An answer contains a question outside this challenge");
            }

            Option option = optionRepository
                    .findById(answer.getOptionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Option not found with id: "
                                    + answer.getOptionId()));

            if (!option.getQuestionId().equals(question.getId())) {
                throw new IllegalStateException(
                        "Selected option does not belong to its question");
            }

            if (Boolean.TRUE.equals(option.getIsCorrect())) {
                score++;
            }
        }

        attempt.setScore(score);
        attempt.setCompletedAt(LocalDateTime.now());

        Attempt completedAttempt = attemptRepository.save(attempt);

        return convertToResponseDTO(completedAttempt);
    }

    @Transactional
    public void deleteAttempt(Long id) {

        Attempt attempt = findAttempt(id);

        if (attempt.getCompletedAt() != null) {
            throw new IllegalStateException(
                    "Cannot delete a completed attempt");
        }

        
        List<Answer> answers = answerRepository.findByAttemptId(id);
        answerRepository.deleteAll(answers);

        attemptRepository.delete(attempt);
    }

    private Attempt findAttempt(Long id) {
        return attemptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attempt not found with id: " + id));
    }

    private AttemptResponseDTO convertToResponseDTO(Attempt attempt) {
        return new AttemptResponseDTO(
                attempt.getId(),
                attempt.getUserId(),
                attempt.getChallengeId(),
                attempt.getStartedAt(),
                attempt.getCompletedAt(),
                attempt.getScore());
    }
}
