package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import questforge.dto.OptionRequestDTO;
import questforge.dto.OptionResponseDTO;
import questforge.service.OptionService;

import java.util.List;

@RestController
@RequestMapping("/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(
            OptionService optionService) {

        this.optionService = optionService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<OptionResponseDTO> createOption(
            @RequestBody @Valid OptionRequestDTO request) {

        return ResponseEntity.ok(
                optionService.createOption(request)
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<OptionResponseDTO>> getAllOptions() {

        return ResponseEntity.ok(
                optionService.getAllOptions()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OptionResponseDTO> getOptionById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                optionService.getOptionById(id)
        );
    }

    // GET OPTIONS BY QUESTION
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<OptionResponseDTO>> getOptionsByQuestion(
            @PathVariable Integer questionId) {

        return ResponseEntity.ok(
                optionService.getOptionsByQuestion(questionId)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<OptionResponseDTO> updateOption(
            @PathVariable Integer id,
            @RequestBody @Valid OptionRequestDTO request) {

        return ResponseEntity.ok(
                optionService.updateOption(
                        id,
                        request
                )
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOption(
            @PathVariable Integer id) {

        optionService.deleteOption(id);

        return ResponseEntity.ok(
                "Option deleted successfully"
        );
    }
}