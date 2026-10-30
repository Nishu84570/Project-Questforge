package questforge.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import questforge.dto.GlobalLeaderboardEntryDTO;
import questforge.dto.LeaderboardEntryDTO;
import questforge.service.LeaderboardService;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(
            LeaderboardService leaderboardService) {

        this.leaderboardService =
                leaderboardService;
    }

    // ==========================================
    // CHALLENGE LEADERBOARD
    // ==========================================

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<LeaderboardEntryDTO>>
    getChallengeLeaderboard(
            @PathVariable Integer challengeId) {

        return ResponseEntity.ok(
                leaderboardService
                        .getChallengeLeaderboard(
                                challengeId
                        )
        );
    }

    // ==========================================
    // GLOBAL LEADERBOARD
    // ==========================================

    @GetMapping("/global")
    public ResponseEntity<List<GlobalLeaderboardEntryDTO>>
    getGlobalLeaderboard() {

        return ResponseEntity.ok(
                leaderboardService
                        .getGlobalLeaderboard()
        );
    }
}