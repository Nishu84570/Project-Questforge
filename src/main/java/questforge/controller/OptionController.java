
package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import questforge.dto.OptionRequestDTO;
import questforge.dto.OptionResponseDTO;
import questforge.service.OptionService;

import java.util.List;

@RestController
@RequestMapping("/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<OptionResponseDTO> createOption(
            @Valid @RequestBody OptionRequestDTO request) {
        return ResponseEntity.ok(optionService.createOption(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OptionResponseDTO>> getAllOptions() {
        return ResponseEntity.ok(optionService.getAllOptions());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OptionResponseDTO> getOptionById(
            @PathVariable Long id) {
        return ResponseEntity.ok(optionService.getOptionById(id));
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<OptionResponseDTO>> getOptionsByQuestion(
            @PathVariable Long questionId) {
        return ResponseEntity.ok(optionService.getOptionsByQuestion(questionId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OptionResponseDTO> updateOption(
            @PathVariable Long id,
            @Valid @RequestBody OptionRequestDTO request) {
        return ResponseEntity.ok(optionService.updateOption(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long id) {
        optionService.deleteOption(id);
        return ResponseEntity.noContent().build();
    }
}
