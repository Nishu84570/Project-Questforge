
package questforge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import questforge.dto.WeeklyQuestResponseDTO;
import questforge.model.User;
import questforge.model.WeeklyQuest;
import questforge.repository.UserRepository;
import questforge.service.WeeklyQuestService;

import java.util.List;

@RestController
@RequestMapping("/weekly-quest")
public class WeeklyQuestController {

    private final WeeklyQuestService weeklyQuestService;
    private final UserRepository userRepository;

    public WeeklyQuestController(
            WeeklyQuestService weeklyQuestService,
            UserRepository userRepository) {
        this.weeklyQuestService = weeklyQuestService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    @GetMapping
    public ResponseEntity<WeeklyQuestResponseDTO> getCurrentWeekQuest(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        WeeklyQuest quest =
                weeklyQuestService.getCurrentWeekQuest(user.getId());

        return ResponseEntity.ok(
                weeklyQuestService.convertToResponseDTO(quest)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<WeeklyQuestResponseDTO>> getQuestHistory(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                weeklyQuestService.getQuestHistory(user.getId())
        );
    }
}
