package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import questforge.dto.AchievementResponseDTO;
import questforge.service.AchievementService;

import java.util.List;

@RestController
@RequestMapping("/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    // =========================
    // CONSTRUCTOR
    // =========================

    public AchievementController(
            AchievementService achievementService) {

        this.achievementService =
                achievementService;
    }

    // =========================
    // GET MY ACHIEVEMENTS
    // =========================

    @GetMapping("/me")
    public ResponseEntity<List<AchievementResponseDTO>>
    getMyAchievements(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                achievementService
                        .getUserAchievements(email)
        );
    }
}