package questforge.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import questforge.dto.SubmissionRequestDTO;
import questforge.dto.SubmissionResponseDTO;
import questforge.dto.SubmissionStatusRequestDTO;

import questforge.service.SubmissionService;

import java.util.List;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(
            SubmissionService submissionService) {

        this.submissionService =
                submissionService;
    }

    // =========================
    // CREATE SUBMISSION
    // =========================

    @PostMapping
    public ResponseEntity<SubmissionResponseDTO> createSubmission(
            @RequestBody @Valid SubmissionRequestDTO request,
            Authentication authentication) {

        String email =
                authentication.getName();

        SubmissionResponseDTO response =
                submissionService.createSubmission(
                        request,
                        email
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // GET MY SUBMISSION BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponseDTO> getMySubmissionById(
            @PathVariable Integer id,
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                submissionService.getMySubmissionById(
                        id,
                        email
                )
        );
    }

    // =========================
    // MY SUBMISSIONS
    // =========================

    @GetMapping("/my")
    public ResponseEntity<List<SubmissionResponseDTO>> getMySubmissions(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                submissionService.getMySubmissions(
                        email
                )
        );
    }

    // =========================
    // ALL SUBMISSIONS
    // =========================

    @GetMapping
    public ResponseEntity<List<SubmissionResponseDTO>> getAllSubmissions() {

        return ResponseEntity.ok(
                submissionService.getAllSubmissions()
        );
    }

    // =========================
    // UPDATE STATUS
    // =========================

    @PutMapping("/{id}/status")
    public ResponseEntity<SubmissionResponseDTO> updateSubmissionStatus(
            @PathVariable Integer id,
            @RequestBody @Valid SubmissionStatusRequestDTO request) {

        return ResponseEntity.ok(
                submissionService.updateSubmissionStatus(
                        id,
                        request
                )
        );
    }
}