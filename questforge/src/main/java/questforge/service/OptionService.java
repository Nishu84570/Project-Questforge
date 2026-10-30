package questforge.service;

import org.springframework.stereotype.Service;
import questforge.dto.OptionRequestDTO;
import questforge.dto.OptionResponseDTO;
import questforge.model.Option;
import questforge.repository.OptionRepository;

import java.util.List;

@Service
public class OptionService {

    private final OptionRepository optionRepository;

    public OptionService(
            OptionRepository optionRepository) {

        this.optionRepository = optionRepository;
    }

    // CREATE
    public OptionResponseDTO createOption(
            OptionRequestDTO request) {

        Option option = new Option();

        option.setQuestionId(
                request.getQuestionId()
        );

        option.setOptionText(
                request.getOptionText()
        );

        option.setIsCorrect(
                request.getIsCorrect()
        );

        Option savedOption =
                optionRepository.save(option);

        return convertToResponseDTO(savedOption);
    }

    // GET ALL
    public List<OptionResponseDTO> getAllOptions() {

        return optionRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // GET BY ID
    public OptionResponseDTO getOptionById(
            Integer id) {

        Option option =
                optionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Option not found"
                                )
                        );

        return convertToResponseDTO(option);
    }

    // GET OPTIONS BY QUESTION
    public List<OptionResponseDTO> getOptionsByQuestion(
            Integer questionId) {

        return optionRepository
                .findByQuestionId(questionId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // UPDATE
    public OptionResponseDTO updateOption(
            Integer id,
            OptionRequestDTO request) {

        Option existingOption =
                optionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Option not found"
                                )
                        );

        existingOption.setQuestionId(
                request.getQuestionId()
        );

        existingOption.setOptionText(
                request.getOptionText()
        );

        existingOption.setIsCorrect(
                request.getIsCorrect()
        );

        Option updatedOption =
                optionRepository.save(existingOption);

        return convertToResponseDTO(updatedOption);
    }

    // DELETE
    public void deleteOption(Integer id) {

        Option existingOption =
                optionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Option not found"
                                )
                        );

        optionRepository.delete(existingOption);
    }

    // ENTITY → RESPONSE DTO
    private OptionResponseDTO convertToResponseDTO(
            Option option) {

        return new OptionResponseDTO(
                option.getId(),
                option.getQuestionId(),
                option.getOptionText(),
                option.getIsCorrect()
        );
    }
}