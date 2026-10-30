
package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import questforge.dto.ChallengeRequestDTO;
import questforge.dto.ChallengeResponseDTO;
import questforge.service.ChallengeService;

import java.util.List;

@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ChallengeResponseDTO> createChallenge(
            @Valid @RequestBody ChallengeRequestDTO request) {
        return ResponseEntity.ok(
                challengeService.createChallenge(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ChallengeResponseDTO>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponseDTO> getChallengeById(
            @PathVariable Long id) {
        return ResponseEntity.ok(challengeService.getChallengeById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ChallengeResponseDTO> updateChallenge(
            @PathVariable Long id,
            @Valid @RequestBody ChallengeRequestDTO request) {
        return ResponseEntity.ok(
                challengeService.updateChallenge(id, request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }
}
