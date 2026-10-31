
package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public AnswerResponseDTO createAnswer(AnswerRequestDTO request) {

        Attempt attempt = findAttempt(request.getAttemptId());
        ensureAttemptOpen(attempt);

        Question question = findQuestion(request.getQuestionId());
        validateQuestionForAttempt(question, attempt);

        if (answerRepository.existsByAttemptIdAndQuestionId(
                request.getAttemptId(), request.getQuestionId())) {
            throw new IllegalStateException(
                    "Answer already exists for this question in this attempt");
        }

        Option option = findOption(request.getOptionId());
        validateOptionForQuestion(option, question);

        Answer answer = new Answer();
        answer.setAttemptId(request.getAttemptId());
        answer.setQuestionId(request.getQuestionId());
        answer.setOptionId(request.getOptionId());

        return convertToResponseDTO(answerRepository.save(answer));
    }

    @Transactional(readOnly = true)
    public List<AnswerResponseDTO> getAllAnswers() {
        return answerRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnswerResponseDTO getAnswerById(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Answer not found with id: " + id));

        return convertToResponseDTO(answer);
    }

    @Transactional(readOnly = true)
    public List<AnswerResponseDTO> getAnswersByAttempt(Long attemptId) {

        if (!attemptRepository.existsById(attemptId)) {
            throw new ResourceNotFoundException(
                    "Attempt not found with id: " + attemptId);
        }

        return answerRepository.findByAttemptId(attemptId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional
    public AnswerResponseDTO updateAnswer(
            Long id,
            AnswerRequestDTO request) {

        Answer existingAnswer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Answer not found with id: " + id));

        Attempt originalAttempt = findAttempt(existingAnswer.getAttemptId());
        ensureAttemptOpen(originalAttempt);

        Attempt targetAttempt = findAttempt(request.getAttemptId());
        ensureAttemptOpen(targetAttempt);

        Question question = findQuestion(request.getQuestionId());
        validateQuestionForAttempt(question, targetAttempt);

        boolean sameCombination =
                existingAnswer.getAttemptId().equals(request.getAttemptId())
                && existingAnswer.getQuestionId().equals(
                        request.getQuestionId());

        if (!sameCombination
                && answerRepository.existsByAttemptIdAndQuestionId(
                        request.getAttemptId(), request.getQuestionId())) {
            throw new IllegalStateException(
                    "Answer already exists for this question in this attempt");
        }

        Option option = findOption(request.getOptionId());
        validateOptionForQuestion(option, question);

        existingAnswer.setAttemptId(request.getAttemptId());
        existingAnswer.setQuestionId(request.getQuestionId());
        existingAnswer.setOptionId(request.getOptionId());

        return convertToResponseDTO(
                answerRepository.save(existingAnswer));
    }

    @Transactional
    public void deleteAnswer(Long id) {

        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Answer not found with id: " + id));

        Attempt attempt = findAttempt(answer.getAttemptId());
        ensureAttemptOpen(attempt);

        answerRepository.delete(answer);
    }

    private Attempt findAttempt(Long attemptId) {
        return attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attempt not found with id: " + attemptId));
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question not found with id: " + questionId));
    }

    private Option findOption(Long optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Option not found with id: " + optionId));
    }

    private void ensureAttemptOpen(Attempt attempt) {
        if (attempt.getCompletedAt() != null) {
            throw new IllegalStateException(
                    "Cannot modify answers in a completed attempt");
        }
    }

    private void validateQuestionForAttempt(
            Question question,
            Attempt attempt) {

        if (!question.getChallengeId().equals(attempt.getChallengeId())) {
            throw new IllegalStateException(
                    "Question does not belong to the attempt's challenge");
        }
    }

    private void validateOptionForQuestion(
            Option option,
            Question question) {

        if (!option.getQuestionId().equals(question.getId())) {
            throw new IllegalStateException(
                    "Option does not belong to the given question");
        }
    }

    private AnswerResponseDTO convertToResponseDTO(Answer answer) {
        return new AnswerResponseDTO(
                answer.getId(),
                answer.getAttemptId(),
                answer.getQuestionId(),
                answer.getOptionId());
    }
}
