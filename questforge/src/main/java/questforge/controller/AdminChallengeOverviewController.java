package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import questforge.dto.AdminChallengeOverviewDTO;
import questforge.service.AdminChallengeOverviewService;

import java.util.List;

@RestController
@RequestMapping("/admin/challenges")
public class AdminChallengeOverviewController {

    private final AdminChallengeOverviewService
            adminChallengeOverviewService;

    public AdminChallengeOverviewController(
            AdminChallengeOverviewService
                    adminChallengeOverviewService) {

        this.adminChallengeOverviewService =
                adminChallengeOverviewService;
    }

    @GetMapping
    public ResponseEntity<List<AdminChallengeOverviewDTO>>
    getChallengeOverview() {

        return ResponseEntity.ok(
                adminChallengeOverviewService
                        .getChallengeOverview()
        );
    }
}