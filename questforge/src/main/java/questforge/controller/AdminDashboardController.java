package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import questforge.dto.AdminDashboardResponseDTO;
import questforge.service.AdminDashboardService;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService) {

        this.adminDashboardService =
                adminDashboardService;
    }

    @GetMapping
    public ResponseEntity<AdminDashboardResponseDTO>
    getDashboardStats() {

        return ResponseEntity.ok(
                adminDashboardService
                        .getDashboardStats()
        );
    }
}