
package questforge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import questforge.model.User;
import questforge.model.UserStreak;
import questforge.repository.UserRepository;
import questforge.service.StreakService;

@RestController
@RequestMapping("/streak")
public class StreakController {

    private final StreakService streakService;
    private final UserRepository userRepository;

    public StreakController(
            StreakService streakService,
            UserRepository userRepository) {
        this.streakService = streakService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<UserStreak> getMyStreak(
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return ResponseEntity.ok(
                streakService.getUserStreak(user.getId())
        );
    }
}
