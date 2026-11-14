
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import questforge.dto.NotificationBroadcastRequestDTO;
import questforge.dto.NotificationResponseDTO;
import questforge.service.NotificationService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private NotificationController notificationController;

    private static final String EMAIL = "user@example.com";
    private static final Long NOTIFICATION_ID = 10L;

    @Test
    void getMyNotifications_shouldReturnNotifications() {
        List<NotificationResponseDTO> notifications =
                List.of(mock(NotificationResponseDTO.class));

        when(authentication.getName()).thenReturn(EMAIL);
        when(notificationService.getMyNotifications(EMAIL))
                .thenReturn(notifications);

        ResponseEntity<List<NotificationResponseDTO>> response =
                notificationController.getMyNotifications(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(notifications, response.getBody());

        verify(notificationService).getMyNotifications(EMAIL);
    }

    @Test
    void getMyNotifications_shouldReturnEmptyList() {
        when(authentication.getName()).thenReturn(EMAIL);
        when(notificationService.getMyNotifications(EMAIL))
                .thenReturn(List.of());

        ResponseEntity<List<NotificationResponseDTO>> response =
                notificationController.getMyNotifications(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getMyUnreadNotifications_shouldReturnUnreadNotifications() {
        List<NotificationResponseDTO> notifications =
                List.of(mock(NotificationResponseDTO.class));

        when(authentication.getName()).thenReturn(EMAIL);
        when(notificationService.getMyUnreadNotifications(EMAIL))
                .thenReturn(notifications);

        ResponseEntity<List<NotificationResponseDTO>> response =
                notificationController.getMyUnreadNotifications(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(notifications, response.getBody());

        verify(notificationService).getMyUnreadNotifications(EMAIL);
    }

    @Test
    void getUnreadNotificationCount_shouldReturnCount() {
        when(authentication.getName()).thenReturn(EMAIL);
        when(notificationService.getUnreadNotificationCount(EMAIL))
                .thenReturn(5L);

        ResponseEntity<Map<String, Long>> response =
                notificationController.getUnreadNotificationCount(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5L, response.getBody().get("count"));

        verify(notificationService).getUnreadNotificationCount(EMAIL);
    }

    @Test
    void getUnreadNotificationCount_shouldReturnZero() {
        when(authentication.getName()).thenReturn(EMAIL);
        when(notificationService.getUnreadNotificationCount(EMAIL))
                .thenReturn(0L);

        ResponseEntity<Map<String, Long>> response =
                notificationController.getUnreadNotificationCount(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, response.getBody().get("count"));
    }

    @Test
    void markAsRead_shouldReturnUpdatedNotification() {
        NotificationResponseDTO notification =
                mock(NotificationResponseDTO.class);

        when(authentication.getName()).thenReturn(EMAIL);
        when(notificationService.markAsRead(NOTIFICATION_ID, EMAIL))
                .thenReturn(notification);

        ResponseEntity<NotificationResponseDTO> response =
                notificationController.markAsRead(
                        NOTIFICATION_ID, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(notification, response.getBody());

        verify(notificationService).markAsRead(NOTIFICATION_ID, EMAIL);
    }

    @Test
    void markAllAsRead_shouldReturnUpdatedCount() {
        when(authentication.getName()).thenReturn(EMAIL);
        when(notificationService.markAllAsRead(EMAIL)).thenReturn(3);

        ResponseEntity<Map<String, Object>> response =
                notificationController.markAllAsRead(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "All notifications marked as read",
                response.getBody().get("message")
        );
        assertEquals(3, response.getBody().get("updatedCount"));

        verify(notificationService).markAllAsRead(EMAIL);
    }

    @Test
    void broadcastNotification_shouldReturnBroadcastCount() {
        NotificationBroadcastRequestDTO request =
                mock(NotificationBroadcastRequestDTO.class);

        when(notificationService.broadcastNotification(request))
                .thenReturn(10);

        ResponseEntity<Map<String, Object>> response =
                notificationController.broadcastNotification(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "Notification broadcast successfully",
                response.getBody().get("message")
        );
        assertEquals(10, response.getBody().get("notificationCount"));

        verify(notificationService).broadcastNotification(request);
    }

    @Test
    void broadcastNotification_shouldHandleZeroRecipients() {
        NotificationBroadcastRequestDTO request =
                mock(NotificationBroadcastRequestDTO.class);

        when(notificationService.broadcastNotification(request))
                .thenReturn(0);

        ResponseEntity<Map<String, Object>> response =
                notificationController.broadcastNotification(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().get("notificationCount"));
    }

    @Test
    void deleteNotification_shouldReturnNoContent() {
        when(authentication.getName()).thenReturn(EMAIL);

        ResponseEntity<Void> response =
                notificationController.deleteNotification(
                        NOTIFICATION_ID, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(notificationService)
                .deleteNotification(NOTIFICATION_ID, EMAIL);
    }
}
