package questforge.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import questforge.dto.AdminSubmissionResponseDTO;
import questforge.dto.AdminSubmissionStatusRequestDTO;
import questforge.service.AdminSubmissionService;

import java.util.List;

@RestController
@RequestMapping("/admin/submissions")
public class AdminSubmissionController {

    private final AdminSubmissionService
            adminSubmissionService;

    public AdminSubmissionController(
            AdminSubmissionService adminSubmissionService) {

        this.adminSubmissionService =
                adminSubmissionService;
    }

    @GetMapping
    public ResponseEntity<List<AdminSubmissionResponseDTO>>
    getSubmissions(
            @RequestParam(required = false)
            String status) {

        if (status == null ||
                status.isBlank()) {

            return ResponseEntity.ok(
                    adminSubmissionService
                            .getAllSubmissions()
            );
        }

        return ResponseEntity.ok(
                adminSubmissionService
                        .getSubmissionsByStatus(status)
        );
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<AdminSubmissionResponseDTO>
    getSubmissionById(
            @PathVariable
            Integer submissionId) {

        return ResponseEntity.ok(
                adminSubmissionService
                        .getSubmissionById(
                                submissionId
                        )
        );
    }

    @PutMapping("/{submissionId}/status")
    public ResponseEntity<AdminSubmissionResponseDTO>
    updateSubmissionStatus(
            @PathVariable
            Integer submissionId,
            @Valid
            @RequestBody
            AdminSubmissionStatusRequestDTO request) {

        return ResponseEntity.ok(
                adminSubmissionService
                        .updateSubmissionStatus(
                                submissionId,
                                request.getStatus()
                        )
        );
    }
}