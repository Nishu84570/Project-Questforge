package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        this.weeklyQuestService =
                weeklyQuestService;

        this.userRepository =
                userRepository;
    }

    // ==========================================
    // GET CURRENT WEEK'S QUEST
    // ==========================================

    @GetMapping
    public ResponseEntity<WeeklyQuestResponseDTO>
    getCurrentWeekQuest(
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

        WeeklyQuest quest =
                weeklyQuestService
                        .getCurrentWeekQuest(
                                user.getId()
                        );

        return ResponseEntity.ok(
                weeklyQuestService
                        .convertToResponseDTO(
                                quest
                        )
        );
    }

    // ==========================================
    // GET WEEKLY QUEST HISTORY
    // ==========================================

    @GetMapping("/history")
    public ResponseEntity<List<WeeklyQuestResponseDTO>>
    getQuestHistory(
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
                weeklyQuestService
                        .getQuestHistory(
                                user.getId()
                        )
        );
    }
}