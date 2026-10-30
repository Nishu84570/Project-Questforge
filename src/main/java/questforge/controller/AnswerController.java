
package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import questforge.dto.AnswerRequestDTO;
import questforge.dto.AnswerResponseDTO;
import questforge.service.AnswerService;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping
    public ResponseEntity<AnswerResponseDTO> createAnswer(
            @Valid @RequestBody AnswerRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.ok(
                answerService.createAnswer(request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AnswerResponseDTO>> getAllAnswers() {
        return ResponseEntity.ok(answerService.getAllAnswers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AnswerResponseDTO> getAnswerById(
            @PathVariable Long id) {
        return ResponseEntity.ok(answerService.getAnswerById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/attempt/{attemptId}")
    public ResponseEntity<List<AnswerResponseDTO>> getAnswersByAttempt(
            @PathVariable Long attemptId) {
        return ResponseEntity.ok(
                answerService.getAnswersByAttempt(attemptId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnswerResponseDTO> updateAnswer(
            @PathVariable Long id,
            @Valid @RequestBody AnswerRequestDTO request,
            Authentication authentication) {
        return ResponseEntity.ok(
                answerService.updateAnswer(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long id,
            Authentication authentication) {
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }
}
