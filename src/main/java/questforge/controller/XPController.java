
package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import questforge.model.User;
import questforge.service.XPService;

@RestController
@RequestMapping("/xp")
public class XPController {

    private final XPService xpService;

    public XPController(XPService xpService) {
        this.xpService = xpService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add/{userId}")
    public ResponseEntity<User> addXp(
            @PathVariable Long userId,
            @RequestParam long xpAmount) {

        return ResponseEntity.ok(
                xpService.addXp(userId, xpAmount)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/challenge/{userId}")
    public ResponseEntity<User> awardChallengeXp(
            @PathVariable Long userId,
            @RequestParam String difficulty) {

        return ResponseEntity.ok(
                xpService.awardChallengeXp(userId, difficulty)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/daily-quest/{userId}")
    public ResponseEntity<User> awardDailyQuestXp(
            @PathVariable Long userId,
            @RequestParam long rewardXp) {

        return ResponseEntity.ok(
                xpService.awardDailyQuestXp(userId, rewardXp)
        );
    }

    @GetMapping("/calculate-level")
    public ResponseEntity<Integer> calculateLevel(
            @RequestParam long xp) {

        return ResponseEntity.ok(
                xpService.calculateLevel(xp)
        );
    }

    @GetMapping("/challenge-reward")
    public ResponseEntity<Long> calculateChallengeXp(
            @RequestParam String difficulty) {

        return ResponseEntity.ok(
                xpService.calculateChallengeXp(difficulty)
        );
    }
}
