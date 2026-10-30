package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import questforge.dto.ProfileProgressDTO;
import questforge.service.ProfileService;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(
            ProfileService profileService) {

        this.profileService =
                profileService;
    }

    // ==========================================
    // MY PROFILE PROGRESS
    // ==========================================

    @GetMapping("/progress")
    public ResponseEntity<ProfileProgressDTO>
    getMyProfileProgress(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                profileService
                        .getMyProfileProgress(email)
        );
    }
}