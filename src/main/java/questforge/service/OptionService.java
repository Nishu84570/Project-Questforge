package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.OptionRequestDTO;
import questforge.dto.OptionResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Option;
import questforge.repository.OptionRepository;
import questforge.repository.QuestionRepository;

import java.util.List;

@Service
public class OptionService {

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;

    public OptionService(
            OptionRepository optionRepository,
            QuestionRepository questionRepository) {
        this.optionRepository = optionRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public OptionResponseDTO createOption(OptionRequestDTO request) {
        validateQuestionExists(request.getQuestionId());

        Option option = new Option();
        option.setQuestionId(request.getQuestionId());
        option.setOptionText(request.getOptionText().trim());
        option.setIsCorrect(request.getIsCorrect());

        return convertToResponseDTO(optionRepository.save(option));
    }

    @Transactional(readOnly = true)
    public List<OptionResponseDTO> getAllOptions() {
        return optionRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public OptionResponseDTO getOptionById(Long id) {
        return convertToResponseDTO(findOptionById(id));
    }

    @Transactional(readOnly = true)
    public List<OptionResponseDTO> getOptionsByQuestion(Long questionId) {
        validateQuestionExists(questionId);

        return optionRepository.findByQuestionId(questionId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional
    public OptionResponseDTO updateOption(
            Long id,
            OptionRequestDTO request) {

        validateQuestionExists(request.getQuestionId());

        Option existingOption = findOptionById(id);
        existingOption.setQuestionId(request.getQuestionId());
        existingOption.setOptionText(request.getOptionText().trim());
        existingOption.setIsCorrect(request.getIsCorrect());

        return convertToResponseDTO(
                optionRepository.save(existingOption)
        );
    }

    @Transactional
    public void deleteOption(Long id) {
        Option existingOption = findOptionById(id);
        optionRepository.delete(existingOption);
    }

    private Option findOptionById(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Option not found with ID: " + id
                        )
                );
    }

    private void validateQuestionExists(Long questionId) {
        if (questionId == null || !questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException(
                    "Question not found with ID: " + questionId
            );
        }
    }

    private OptionResponseDTO convertToResponseDTO(Option option) {
        return new OptionResponseDTO(
                option.getId(),
                option.getQuestionId(),
                option.getOptionText(),
                option.getIsCorrect()
        );
    }
}