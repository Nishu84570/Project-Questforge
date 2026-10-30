package questforge.service;

import org.springframework.stereotype.Service;
import questforge.dto.QuestionRequestDTO;
import questforge.dto.QuestionResponseDTO;
import questforge.model.Question;
import questforge.repository.OptionRepository;
import questforge.repository.QuestionRepository;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    public QuestionService(
            QuestionRepository questionRepository,
            OptionRepository optionRepository) {

        this.questionRepository =
                questionRepository;

        this.optionRepository =
                optionRepository;
    }

    // CREATE
    public QuestionResponseDTO createQuestion(
            QuestionRequestDTO request) {

        Question question = new Question();

        question.setChallengeId(
                request.getChallengeId()
        );

        question.setQuestionText(
                request.getQuestionText()
        );

        Question savedQuestion =
                questionRepository.save(question);

        return convertToResponseDTO(savedQuestion);
    }

    // GET ALL
    public List<QuestionResponseDTO> getAllQuestions() {

        return questionRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // GET BY ID
    public QuestionResponseDTO getQuestionById(
            Integer id) {

        Question question =
                questionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question not found"
                                )
                        );

        return convertToResponseDTO(question);
    }

    // GET BY CHALLENGE
    public List<QuestionResponseDTO> getQuestionsByChallenge(
            Integer challengeId) {

        return questionRepository
                .findByChallengeId(challengeId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // UPDATE
    public QuestionResponseDTO updateQuestion(
            Integer id,
            QuestionRequestDTO request) {

        Question existingQuestion =
                questionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question not found"
                                )
                        );

        existingQuestion.setChallengeId(
                request.getChallengeId()
        );

        existingQuestion.setQuestionText(
                request.getQuestionText()
        );

        Question updatedQuestion =
                questionRepository.save(
                        existingQuestion
                );

        return convertToResponseDTO(
                updatedQuestion
        );
    }

    // DELETE
    public void deleteQuestion(Integer id) {

        Question existingQuestion =
                questionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question not found"
                                )
                        );

        // Delete options first because
        // options reference this question.
        optionRepository.deleteByQuestionId(id);

        questionRepository.delete(existingQuestion);
    }

    // ENTITY → RESPONSE DTO
    private QuestionResponseDTO convertToResponseDTO(
            Question question) {

        return new QuestionResponseDTO(
                question.getId(),
                question.getChallengeId(),
                question.getQuestionText()
        );
    }
}