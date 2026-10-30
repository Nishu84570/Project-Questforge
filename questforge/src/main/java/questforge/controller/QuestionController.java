package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import questforge.dto.QuestionRequestDTO;
import questforge.dto.QuestionResponseDTO;
import questforge.service.QuestionService;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<QuestionResponseDTO> createQuestion(
            @RequestBody @Valid QuestionRequestDTO request) {

        return ResponseEntity.ok(
                questionService.createQuestion(request)
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<QuestionResponseDTO>> getAllQuestions() {

        return ResponseEntity.ok(
                questionService.getAllQuestions()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                questionService.getQuestionById(id)
        );
    }

    // GET QUESTIONS BY CHALLENGE
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByChallenge(
            @PathVariable Integer challengeId) {

        return ResponseEntity.ok(
                questionService.getQuestionsByChallenge(challengeId)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(
            @PathVariable Integer id,
            @RequestBody @Valid QuestionRequestDTO request) {

        return ResponseEntity.ok(
                questionService.updateQuestion(id, request)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(
            @PathVariable Integer id) {

        questionService.deleteQuestion(id);

        return ResponseEntity.ok(
                "Question deleted successfully"
        );
    }
}