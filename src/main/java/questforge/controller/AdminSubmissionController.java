
package questforge.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import questforge.dto.AdminSubmissionResponseDTO;
import questforge.dto.AdminSubmissionStatusRequestDTO;
import questforge.service.AdminSubmissionService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/submissions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubmissionController {

    private final AdminSubmissionService adminSubmissionService;

    public AdminSubmissionController(
            AdminSubmissionService adminSubmissionService) {
        this.adminSubmissionService = adminSubmissionService;
    }

    @GetMapping
    public ResponseEntity<List<AdminSubmissionResponseDTO>> getSubmissions(
            @RequestParam(required = false) String status) {

        if (status == null || status.isBlank()) {
            return ResponseEntity.ok(
                    adminSubmissionService.getAllSubmissions());
        }

        return ResponseEntity.ok(
                adminSubmissionService.getSubmissionsByStatus(status.trim()));
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<AdminSubmissionResponseDTO> getSubmissionById(
            @PathVariable Long submissionId) {

        return ResponseEntity.ok(
                adminSubmissionService.getSubmissionById(submissionId));
    }

    @PutMapping("/{submissionId}/status")
    public ResponseEntity<AdminSubmissionResponseDTO> updateSubmissionStatus(
            @PathVariable Long submissionId,
            @Valid @RequestBody AdminSubmissionStatusRequestDTO request) {

        return ResponseEntity.ok(
                adminSubmissionService.updateSubmissionStatus(
                        submissionId, request.getStatus()));
    }
}
