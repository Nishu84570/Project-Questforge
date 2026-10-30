
package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import questforge.dto.ProfileProgressDTO;
import questforge.service.ProfileService;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/progress")
    public ResponseEntity<ProfileProgressDTO> getMyProfileProgress(
            Authentication authentication) {
        return ResponseEntity.ok(
                profileService.getMyProfileProgress(authentication.getName())
        );
    }
}
