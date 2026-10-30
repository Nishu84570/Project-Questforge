package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    // CREATE
    @PostMapping
    public ResponseEntity<AttemptResponseDTO> createAttempt(
            @RequestBody @Valid AttemptRequestDTO request) {

        return ResponseEntity.ok(
                attemptService.createAttempt(request)
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<AttemptResponseDTO>> getAllAttempts() {

        return ResponseEntity.ok(
                attemptService.getAllAttempts()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<AttemptResponseDTO> getAttemptById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                attemptService.getAttemptById(id)
        );
    }

    // GET ATTEMPTS BY USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttemptResponseDTO>> getAttemptsByUser(
            @PathVariable Integer userId) {

        return ResponseEntity.ok(
                attemptService.getAttemptsByUser(userId)
        );
    }

    // GET ATTEMPTS BY CHALLENGE
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<AttemptResponseDTO>> getAttemptsByChallenge(
            @PathVariable Integer challengeId) {

        return ResponseEntity.ok(
                attemptService.getAttemptsByChallenge(challengeId)
        );
    }

    // COMPLETE ATTEMPT
    @PutMapping("/{id}/complete")
    public ResponseEntity<AttemptResponseDTO> completeAttempt(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                attemptService.completeAttempt(id)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttempt(
            @PathVariable Integer id) {

        attemptService.deleteAttempt(id);

        return ResponseEntity.ok(
                "Attempt deleted successfully"
        );
    }
}