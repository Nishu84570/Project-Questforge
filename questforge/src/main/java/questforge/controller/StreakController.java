package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        this.streakService =
                streakService;

        this.userRepository =
                userRepository;
    }

    // ==========================================
    // GET MY STREAK
    // ==========================================

    @GetMapping
    public ResponseEntity<UserStreak>
    getMyStreak(
            Authentication authentication) {

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "User not found"
                                )
                        );

        return ResponseEntity.ok(
                streakService.getUserStreak(
                        user.getId()
                )
        );
    }
}