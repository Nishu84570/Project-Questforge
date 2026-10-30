
package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import questforge.dto.NotificationBroadcastRequestDTO;
import questforge.dto.NotificationResponseDTO;
import questforge.service.NotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications(
            Authentication authentication) {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(authentication.getName())
        );
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getMyUnreadNotifications(
            Authentication authentication) {
        return ResponseEntity.ok(
                notificationService.getMyUnreadNotifications(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadNotificationCount(
            Authentication authentication) {
        long count = notificationService.getUnreadNotificationCount(
                authentication.getName()
        );

        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {
        return ResponseEntity.ok(
                notificationService.markAsRead(
                        notificationId, authentication.getName()
                )
        );
    }

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            Authentication authentication) {
        int updatedCount = notificationService.markAllAsRead(
                authentication.getName()
        );

        return ResponseEntity.ok(Map.of(
                "message", "All notifications marked as read",
                "updatedCount", updatedCount
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, Object>> broadcastNotification(
            @Valid @RequestBody NotificationBroadcastRequestDTO request) {
        int notificationCount =
                notificationService.broadcastNotification(request);

        return ResponseEntity.ok(Map.of(
                "message", "Notification broadcast successfully",
                "notificationCount", notificationCount
        ));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId,
            Authentication authentication) {
        notificationService.deleteNotification(
                notificationId, authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
