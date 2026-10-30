package questforge.service;

import org.springframework.stereotype.Service;
import questforge.dto.AnswerRequestDTO;
import questforge.dto.AnswerResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Answer;
import questforge.model.Attempt;
import questforge.model.Option;
import questforge.model.Question;
import questforge.repository.AnswerRepository;
import questforge.repository.AttemptRepository;
import questforge.repository.OptionRepository;
import questforge.repository.QuestionRepository;

import java.util.List;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final AttemptRepository attemptRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    public AnswerService(
            AnswerRepository answerRepository,
            AttemptRepository attemptRepository,
            QuestionRepository questionRepository,
            OptionRepository optionRepository) {

        this.answerRepository = answerRepository;
        this.attemptRepository = attemptRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    // CREATE
    public AnswerResponseDTO createAnswer(AnswerRequestDTO request) {

        Attempt attempt = attemptRepository.findById(
                request.getAttemptId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Attempt not found with id: "
                                + request.getAttemptId()
                )
        );

        if (attempt.getCompletedAt() != null) {
            throw new IllegalStateException(
                    "Cannot add answer to a completed attempt"
            );
        }

        Question question = questionRepository.findById(
                request.getQuestionId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Question not found with id: "
                                + request.getQuestionId()
                )
        );

        if (!question.getChallengeId().equals(attempt.getChallengeId())) {
            throw new IllegalStateException(
                    "Question does not belong to the attempt's challenge"
            );
        }

        // Prevent duplicate answer
        if (answerRepository.existsByAttemptIdAndQuestionId(
                request.getAttemptId(),
                request.getQuestionId())) {

            throw new IllegalStateException(
                    "Answer already exists for this question in this attempt"
            );
        }

        Option option = optionRepository.findById(
                request.getOptionId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Option not found with id: "
                                + request.getOptionId()
                )
        );

        if (!option.getQuestionId().equals(request.getQuestionId())) {
            throw new IllegalStateException(
                    "Option does not belong to the given question"
            );
        }

        Answer answer = new Answer();

        answer.setAttemptId(request.getAttemptId());
        answer.setQuestionId(request.getQuestionId());
        answer.setOptionId(request.getOptionId());

        Answer savedAnswer = answerRepository.save(answer);

        return convertToResponseDTO(savedAnswer);
    }

    // GET ALL
    public List<AnswerResponseDTO> getAllAnswers() {

        return answerRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // GET BY ID
    public AnswerResponseDTO getAnswerById(Integer id) {

        Answer answer = answerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Answer not found with id: " + id
                        )
                );

        return convertToResponseDTO(answer);
    }

    // GET ANSWERS BY ATTEMPT
    public List<AnswerResponseDTO> getAnswersByAttempt(
            Integer attemptId) {

        if (!attemptRepository.existsById(attemptId)) {
            throw new ResourceNotFoundException(
                    "Attempt not found with id: " + attemptId
            );
        }

        return answerRepository.findByAttemptId(attemptId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // UPDATE
    public AnswerResponseDTO updateAnswer(
            Integer id,
            AnswerRequestDTO request) {

        Answer existingAnswer = answerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Answer not found with id: " + id
                        )
                );

        Attempt attempt = attemptRepository.findById(
                request.getAttemptId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Attempt not found with id: "
                                + request.getAttemptId()
                )
        );

        if (attempt.getCompletedAt() != null) {
            throw new IllegalStateException(
                    "Cannot update answer in a completed attempt"
            );
        }

        Question question = questionRepository.findById(
                request.getQuestionId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Question not found with id: "
                                + request.getQuestionId()
                )
        );

        if (!question.getChallengeId().equals(attempt.getChallengeId())) {
            throw new IllegalStateException(
                    "Question does not belong to the attempt's challenge"
            );
        }

        // Check duplicate answer only if moving to
        // a different Attempt + Question combination
        boolean sameCombination =
                existingAnswer.getAttemptId().equals(
                        request.getAttemptId()
                )
                && existingAnswer.getQuestionId().equals(
                        request.getQuestionId()
                );

        if (!sameCombination
                && answerRepository.existsByAttemptIdAndQuestionId(
                        request.getAttemptId(),
                        request.getQuestionId())) {

            throw new IllegalStateException(
                    "Answer already exists for this question in this attempt"
            );
        }

        Option option = optionRepository.findById(
                request.getOptionId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Option not found with id: "
                                + request.getOptionId()
                )
        );

        if (!option.getQuestionId().equals(request.getQuestionId())) {
            throw new IllegalStateException(
                    "Option does not belong to the given question"
            );
        }

        existingAnswer.setAttemptId(request.getAttemptId());
        existingAnswer.setQuestionId(request.getQuestionId());
        existingAnswer.setOptionId(request.getOptionId());

        Answer updatedAnswer =
                answerRepository.save(existingAnswer);

        return convertToResponseDTO(updatedAnswer);
    }

    // DELETE
    public void deleteAnswer(Integer id) {

        Answer answer = answerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Answer not found with id: " + id
                        )
                );

        answerRepository.delete(answer);
    }

    // ENTITY → RESPONSE DTO
    private AnswerResponseDTO convertToResponseDTO(
            Answer answer) {

        return new AnswerResponseDTO(
                answer.getId(),
                answer.getAttemptId(),
                answer.getQuestionId(),
                answer.getOptionId()
        );
    }
}