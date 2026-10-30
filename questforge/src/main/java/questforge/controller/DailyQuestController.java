package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        this.dailyQuestService =
                dailyQuestService;

        this.userRepository =
                userRepository;
    }

    // ==========================================
    // GET TODAY'S DAILY QUEST
    // ==========================================

    @GetMapping
    public ResponseEntity<DailyQuestResponseDTO> getTodayQuest(
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

        DailyQuest quest =
                dailyQuestService
                        .getTodayQuest(
                                user.getId()
                        );

        return ResponseEntity.ok(
                dailyQuestService
                        .convertToResponseDTO(
                                quest
                        )
        );
    }

    // ==========================================
    // GET DAILY QUEST HISTORY
    // ==========================================

    @GetMapping("/history")
    public ResponseEntity<List<DailyQuestResponseDTO>>
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
                dailyQuestService
                        .getQuestHistory(
                                user.getId()
                        )
        );
    }

    // ==========================================
    // COMPLETE DAILY QUEST
    // ==========================================

    @PostMapping("/complete")
    public ResponseEntity<DailyQuestResponseDTO> completeDailyQuest(
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

        DailyQuest quest =
                dailyQuestService
                        .recordChallengeCompletion(
                                user.getId()
                        );

        return ResponseEntity.ok(
                dailyQuestService
                        .convertToResponseDTO(
                                quest
                        )
        );
    }
}