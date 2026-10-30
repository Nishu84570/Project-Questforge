
package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import questforge.dto.ChallengeAnalyticsResponseDTO;
import questforge.service.ChallengeAnalyticsService;

@RestController
@RequestMapping("/admin/challenges")
@PreAuthorize("hasRole('ADMIN')")
public class ChallengeAnalyticsController {

    private final ChallengeAnalyticsService challengeAnalyticsService;

    public ChallengeAnalyticsController(
            ChallengeAnalyticsService challengeAnalyticsService) {
        this.challengeAnalyticsService = challengeAnalyticsService;
    }

    @GetMapping("/{challengeId}/analytics")
    public ResponseEntity<ChallengeAnalyticsResponseDTO> getChallengeAnalytics(
            @PathVariable Long challengeId) {
        return ResponseEntity.ok(
                challengeAnalyticsService.getChallengeAnalytics(challengeId)
        );
    }
}
