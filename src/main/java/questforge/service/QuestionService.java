package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.QuestionRequestDTO;
import questforge.dto.QuestionResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Question;
import questforge.repository.ChallengeRepository;
import questforge.repository.OptionRepository;
import questforge.repository.QuestionRepository;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final ChallengeRepository challengeRepository;

    public QuestionService(
            QuestionRepository questionRepository,
            OptionRepository optionRepository,
            ChallengeRepository challengeRepository) {

        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.challengeRepository = challengeRepository;
    }

    @Transactional
    public QuestionResponseDTO createQuestion(QuestionRequestDTO request) {
        validateChallengeExists(request.getChallengeId());

        Question question = new Question();
        question.setChallengeId(request.getChallengeId());
        question.setQuestionText(request.getQuestionText().trim());

        return convertToResponseDTO(questionRepository.save(question));
    }

    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionById(Long id) {
        return convertToResponseDTO(findQuestionById(id));
    }

    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByChallenge(
            Long challengeId) {

        validateChallengeExists(challengeId);

        return questionRepository.findByChallengeId(challengeId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional
    public QuestionResponseDTO updateQuestion(
            Long id,
            QuestionRequestDTO request) {

        validateChallengeExists(request.getChallengeId());

        Question existingQuestion = findQuestionById(id);
        existingQuestion.setChallengeId(request.getChallengeId());
        existingQuestion.setQuestionText(request.getQuestionText().trim());

        return convertToResponseDTO(
                questionRepository.save(existingQuestion)
        );
    }

    @Transactional
    public void deleteQuestion(Long id) {
        Question existingQuestion = findQuestionById(id);
        optionRepository.deleteByQuestionId(id);
        questionRepository.delete(existingQuestion);
    }

    private Question findQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Question not found with ID: " + id
                        )
                );
    }

    private void validateChallengeExists(Long challengeId) {
        if (challengeId == null
                || !challengeRepository.existsById(challengeId)) {

            throw new ResourceNotFoundException(
                    "Challenge not found with ID: " + challengeId
            );
        }
    }

    private QuestionResponseDTO convertToResponseDTO(Question question) {
        return new QuestionResponseDTO(
                question.getId(),
                question.getChallengeId(),
                question.getQuestionText()
        );
    }
}