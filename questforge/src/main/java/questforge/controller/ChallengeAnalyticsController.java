package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import questforge.dto.ChallengeAnalyticsResponseDTO;
import questforge.service.ChallengeAnalyticsService;

@RestController
@RequestMapping("/admin/challenges")
public class ChallengeAnalyticsController {

    private final ChallengeAnalyticsService
            challengeAnalyticsService;

    public ChallengeAnalyticsController(
            ChallengeAnalyticsService challengeAnalyticsService) {

        this.challengeAnalyticsService =
                challengeAnalyticsService;
    }

    @GetMapping("/{challengeId}/analytics")
    public ResponseEntity<ChallengeAnalyticsResponseDTO>
    getChallengeAnalytics(
            @PathVariable Integer challengeId) {

        return ResponseEntity.ok(
                challengeAnalyticsService
                        .getChallengeAnalytics(
                                challengeId
                        )
        );
    }
}