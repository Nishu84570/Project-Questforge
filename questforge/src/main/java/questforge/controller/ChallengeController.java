package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    // CREATE
    @PostMapping
    public ResponseEntity<ChallengeResponseDTO> createChallenge(
            @RequestBody @Valid ChallengeRequestDTO request) {

        return ResponseEntity.ok(
                challengeService.createChallenge(request)
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<ChallengeResponseDTO>> getAllChallenges() {

        return ResponseEntity.ok(
                challengeService.getAllChallenges()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponseDTO> getChallengeById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                challengeService.getChallengeById(id)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ChallengeResponseDTO> updateChallenge(
            @PathVariable Integer id,
            @RequestBody @Valid ChallengeRequestDTO request) {

        return ResponseEntity.ok(
                challengeService.updateChallenge(id, request)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteChallenge(
            @PathVariable Integer id) {

        challengeService.deleteChallenge(id);

        return ResponseEntity.ok(
                "Challenge deleted successfully"
        );
    }
}