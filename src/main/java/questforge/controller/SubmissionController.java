
package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<SubmissionResponseDTO> createSubmission(
            @Valid @RequestBody SubmissionRequestDTO request,
            Authentication authentication) {

        SubmissionResponseDTO response =
                submissionService.createSubmission(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponseDTO> getMySubmissionById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                submissionService.getMySubmissionById(
                        id,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<SubmissionResponseDTO>> getMySubmissions(
            Authentication authentication) {

        return ResponseEntity.ok(
                submissionService.getMySubmissions(
                        authentication.getName()
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SubmissionResponseDTO>> getAllSubmissions() {
        return ResponseEntity.ok(
                submissionService.getAllSubmissions()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<SubmissionResponseDTO> updateSubmissionStatus(
            @PathVariable Long id,
            @Valid @RequestBody SubmissionStatusRequestDTO request) {

        return ResponseEntity.ok(
                submissionService.updateSubmissionStatus(id, request)
        );
    }
}
