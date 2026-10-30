package questforge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import questforge.dto.NotificationBroadcastRequestDTO;
import questforge.dto.NotificationResponseDTO;
import questforge.service.NotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService =
                notificationService;
    }

    // ==========================================
    // GET ALL NOTIFICATIONS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>>
    getMyNotifications(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                notificationService
                        .getMyNotifications(email)
        );
    }

    // ==========================================
    // GET UNREAD NOTIFICATIONS
    // ==========================================

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDTO>>
    getMyUnreadNotifications(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                notificationService
                        .getMyUnreadNotifications(email)
        );
    }

    // ==========================================
    // GET UNREAD NOTIFICATION COUNT
    // ==========================================

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>>
    getUnreadNotificationCount(
            Authentication authentication) {

        String email =
                authentication.getName();

        long count =
                notificationService
                        .getUnreadNotificationCount(
                                email
                        );

        return ResponseEntity.ok(
                Map.of(
                        "count",
                        count
                )
        );
    }

    // ==========================================
    // MARK ONE NOTIFICATION AS READ
    // ==========================================

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponseDTO>
    markAsRead(
            @PathVariable Integer notificationId,
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                notificationService
                        .markAsRead(
                                notificationId,
                                email
                        )
        );
    }

    // ==========================================
    // MARK ALL NOTIFICATIONS AS READ
    // ==========================================

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>>
    markAllAsRead(
            Authentication authentication) {

        String email =
                authentication.getName();

        int updatedCount =
                notificationService
                        .markAllAsRead(
                                email
                        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "All notifications marked as read",
                        "updatedCount",
                        updatedCount
                )
        );
    }

    // ==========================================
    // ADMIN BROADCAST
    // ==========================================

    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, Object>>
    broadcastNotification(
            @Valid @RequestBody
            NotificationBroadcastRequestDTO request) {

        int notificationCount =
                notificationService
                        .broadcastNotification(
                                request
                        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Notification broadcast successfully",
                        "notificationCount",
                        notificationCount
                )
        );
    }

    // ==========================================
    // DELETE NOTIFICATION
    // ==========================================

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void>
    deleteNotification(
            @PathVariable Integer notificationId,
            Authentication authentication) {

        String email =
                authentication.getName();

        notificationService.deleteNotification(
                notificationId,
                email
        );

        return ResponseEntity.noContent()
                .build();
    }
}