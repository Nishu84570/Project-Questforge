
package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import questforge.dto.AttemptRequestDTO;
import questforge.dto.AttemptResponseDTO;
import questforge.service.AttemptService;

import java.util.List;

@RestController
@RequestMapping("/attempts")
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping
    public ResponseEntity<AttemptResponseDTO> createAttempt(
            @Valid @RequestBody AttemptRequestDTO request,
            Authentication authentication) {
        return ResponseEntity.ok(
                attemptService.createAttempt(request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AttemptResponseDTO>> getAllAttempts() {
        return ResponseEntity.ok(attemptService.getAllAttempts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttemptResponseDTO> getAttemptById(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(attemptService.getAttemptById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttemptResponseDTO>> getAttemptsByUser(
            @PathVariable Long userId,
            Authentication authentication) {
        return ResponseEntity.ok(attemptService.getAttemptsByUser(userId));
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<AttemptResponseDTO>> getAttemptsByChallenge(
            @PathVariable Long challengeId,
            Authentication authentication) {
        return ResponseEntity.ok(
                attemptService.getAttemptsByChallenge(challengeId)
        );
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AttemptResponseDTO> completeAttempt(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(attemptService.completeAttempt(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttempt(@PathVariable Long id) {
        attemptService.deleteAttempt(id);
        return ResponseEntity.noContent().build();
    }
}
