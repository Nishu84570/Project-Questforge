package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import questforge.dto.AnswerRequestDTO;
import questforge.dto.AnswerResponseDTO;
import questforge.service.AnswerService;

import java.util.List;

@RestController
@RequestMapping("/answers")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<AnswerResponseDTO> createAnswer(
            @RequestBody @Valid AnswerRequestDTO request) {

        return ResponseEntity.ok(
                answerService.createAnswer(request)
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<AnswerResponseDTO>> getAllAnswers() {

        return ResponseEntity.ok(
                answerService.getAllAnswers()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<AnswerResponseDTO> getAnswerById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                answerService.getAnswerById(id)
        );
    }

    // GET ANSWERS BY ATTEMPT
    @GetMapping("/attempt/{attemptId}")
    public ResponseEntity<List<AnswerResponseDTO>> getAnswersByAttempt(
            @PathVariable Integer attemptId) {

        return ResponseEntity.ok(
                answerService.getAnswersByAttempt(attemptId)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<AnswerResponseDTO> updateAnswer(
            @PathVariable Integer id,
            @RequestBody @Valid AnswerRequestDTO request) {

        return ResponseEntity.ok(
                answerService.updateAnswer(id, request)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAnswer(
            @PathVariable Integer id) {

        answerService.deleteAnswer(id);

        return ResponseEntity.ok(
                "Answer deleted successfully"
        );
    }
}