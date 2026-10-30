
package questforge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import questforge.dto.DailyQuestResponseDTO;
import questforge.model.DailyQuest;
import questforge.model.User;
import questforge.repository.UserRepository;
import questforge.service.DailyQuestService;

import java.util.List;

@RestController
@RequestMapping("/daily-quest")
public class DailyQuestController {

    private final DailyQuestService dailyQuestService;
    private final UserRepository userRepository;

    public DailyQuestController(
            DailyQuestService dailyQuestService,
            UserRepository userRepository) {
        this.dailyQuestService = dailyQuestService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Authentication required"
            );
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));
    }

    @GetMapping
    public ResponseEntity<DailyQuestResponseDTO> getTodayQuest(
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        DailyQuest quest = dailyQuestService.getTodayQuest(user.getId());

        return ResponseEntity.ok(
                dailyQuestService.convertToResponseDTO(quest)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<DailyQuestResponseDTO>> getQuestHistory(
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                dailyQuestService.getQuestHistory(user.getId())
        );
    }

    
    @PostMapping("/complete")
    public ResponseEntity<DailyQuestResponseDTO> completeDailyQuest(
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        DailyQuest quest =
                dailyQuestService.recordChallengeCompletion(user.getId());

        return ResponseEntity.ok(
                dailyQuestService.convertToResponseDTO(quest)
        );
    }
}
