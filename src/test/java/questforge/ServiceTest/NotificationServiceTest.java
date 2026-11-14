
package questforge.ServiceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import questforge.dto.NotificationBroadcastRequestDTO;
import questforge.dto.NotificationResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Notification;
import questforge.model.User;
import questforge.repository.NotificationRepository;
import questforge.repository.UserRepository;
import questforge.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

  

    private User createUser(Long id) {

        User user = mock(User.class);

        // Lenient stubbing avoids unnecessary stubbing errors
        // when a test does not call getId().
        lenient().when(user.getId()).thenReturn(id);

        return user;
    }

    private Notification createNotification(
            Long id,
            Long userId,
            String title,
            String message,
            String type,
            Boolean read) {

        Notification notification = new Notification();

        // Corrected duplicate setUserId()
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(read);
        notification.setCreatedAt(LocalDateTime.now());

        return notification;
    }

    // -------------------------------------------------
    // 1. CREATE NOTIFICATION
    // -------------------------------------------------

    @Test
    void createNotification_Success() {

        Long userId = 1L;
        User user = createUser(userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification notification = invocation.getArgument(0);
                    notification.setUserId(10L);
                    return notification;
                });

        NotificationResponseDTO response =
                notificationService.createNotification(
                        userId,
                        "Welcome",
                        "Welcome to QuestForge",
                        "INFO");

        assertNotNull(response);

        verify(userRepository).findById(userId);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotification_UserNotFound() {

        Long userId = 100L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.createNotification(
                        userId,
                        "Welcome",
                        "Hello",
                        "INFO"));

        verify(notificationRepository, never())
                .save(any(Notification.class));
    }

    @Test
    void createNotification_ShouldSetReadAsFalse() {

        Long userId = 1L;
        User user = createUser(userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> {

                    Notification notification = invocation.getArgument(0);

                    assertEquals(userId, notification.getUserId());
                    assertEquals("Test Title", notification.getTitle());
                    assertEquals("Test Message", notification.getMessage());
                    assertEquals("INFO", notification.getType());
                    assertFalse(notification.getRead());
                    assertNotNull(notification.getCreatedAt());

                    return notification;
                });

        notificationService.createNotification(
                userId,
                "Test Title",
                "Test Message",
                "INFO");

        verify(notificationRepository).save(any(Notification.class));
    }

    // -------------------------------------------------
    // 2. BROADCAST NOTIFICATION
    // -------------------------------------------------

    @Test
    void broadcastNotification_Success() {

        User user1 = createUser(1L);
        User user2 = createUser(2L);
        User user3 = createUser(3L);

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2, user3));

        NotificationBroadcastRequestDTO request =
                mock(NotificationBroadcastRequestDTO.class);

        when(request.getTitle()).thenReturn("Announcement");
        when(request.getMessage()).thenReturn("New update available");
        when(request.getType()).thenReturn("INFO");

        int result = notificationService.broadcastNotification(request);

        assertEquals(3, result);

        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    void broadcastNotification_WhenNoUsersExist() {

        when(userRepository.findAll())
                .thenReturn(List.of());

        NotificationBroadcastRequestDTO request =
                mock(NotificationBroadcastRequestDTO.class);

        int result = notificationService.broadcastNotification(request);

        assertEquals(0, result);

        verify(notificationRepository).saveAll(anyList());

        verify(request, never()).getTitle();
        verify(request, never()).getMessage();
        verify(request, never()).getType();
    }

    @Test
    void broadcastNotification_ShouldCreateNotificationForEveryUser() {

        User user1 = createUser(1L);
        User user2 = createUser(2L);

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        NotificationBroadcastRequestDTO request =
                mock(NotificationBroadcastRequestDTO.class);

        when(request.getTitle()).thenReturn("Update");
        when(request.getMessage()).thenReturn("System update");
        when(request.getType()).thenReturn("SYSTEM");

        when(notificationRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {

                    List<Notification> notifications =
                            invocation.getArgument(0);

                    assertEquals(2, notifications.size());

                    assertEquals(1L, notifications.get(0).getUserId());
                    assertEquals(2L, notifications.get(1).getUserId());

                    for (Notification notification : notifications) {
                        assertEquals("Update", notification.getTitle());
                        assertEquals("System update", notification.getMessage());
                        assertEquals("SYSTEM", notification.getType());
                        assertFalse(notification.getRead());
                        assertNotNull(notification.getCreatedAt());
                    }

                    return notifications;
                });

        int result = notificationService.broadcastNotification(request);

        assertEquals(2, result);
    }

    // -------------------------------------------------
    // 3. GET MY NOTIFICATIONS
    // -------------------------------------------------

    @Test
    void getMyNotifications_Success() {

        String email = "user@gmail.com";
        User user = createUser(1L);

        Notification notification = createNotification(
                10L, 1L, "Welcome", "Hello User", "INFO", false);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(notification));

        List<NotificationResponseDTO> result =
                notificationService.getMyNotifications(email);

        assertEquals(1, result.size());
        assertNotNull(result.get(0));

        verify(notificationRepository)
                .findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getMyNotifications_WhenNoNotificationsExist() {

        String email = "user@gmail.com";
        User user = createUser(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of());

        List<NotificationResponseDTO> result =
                notificationService.getMyNotifications(email);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getMyNotifications_UserNotFound() {

        String email = "unknown@gmail.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.getMyNotifications(email));

        verify(notificationRepository, never())
                .findByUserIdOrderByCreatedAtDesc(anyLong());
    }

    // -------------------------------------------------
    // 4. GET MY UNREAD NOTIFICATIONS
    // -------------------------------------------------

    @Test
    void getMyUnreadNotifications_Success() {

        String email = "user@gmail.com";
        User user = createUser(1L);

        Notification notification = createNotification(
                10L, 1L, "Alert", "New alert", "WARNING", false);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(notification));

        List<NotificationResponseDTO> result =
                notificationService.getMyUnreadNotifications(email);

        assertEquals(1, result.size());
        assertNotNull(result.get(0));

        verify(notificationRepository)
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(1L);
    }

    @Test
    void getMyUnreadNotifications_WhenEmpty() {

        String email = "user@gmail.com";
        User user = createUser(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(1L))
                .thenReturn(List.of());

        List<NotificationResponseDTO> result =
                notificationService.getMyUnreadNotifications(email);

        assertTrue(result.isEmpty());
    }

    @Test
    void getMyUnreadNotifications_UserNotFound() {

        String email = "unknown@gmail.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.getMyUnreadNotifications(email));

        verify(notificationRepository, never())
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(anyLong());
    }

    // -------------------------------------------------
    // 5. GET UNREAD NOTIFICATION COUNT
    // -------------------------------------------------

    @Test
    void getUnreadNotificationCount_Success() {

        String email = "user@gmail.com";
        User user = createUser(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.countByUserIdAndReadFalse(1L))
                .thenReturn(5L);

        long result =
                notificationService.getUnreadNotificationCount(email);

        assertEquals(5L, result);
    }

    @Test
    void getUnreadNotificationCount_WhenZero() {

        String email = "user@gmail.com";
        User user = createUser(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.countByUserIdAndReadFalse(1L))
                .thenReturn(0L);

        long result =
                notificationService.getUnreadNotificationCount(email);

        assertEquals(0L, result);
    }

    @Test
    void getUnreadNotificationCount_UserNotFound() {

        String email = "unknown@gmail.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.getUnreadNotificationCount(email));

        verify(notificationRepository, never())
                .countByUserIdAndReadFalse(anyLong());
    }

    // -------------------------------------------------
    // 6. MARK AS READ
    // -------------------------------------------------

    @Test
    void markAsRead_Success() {

        Long notificationId = 10L;
        String email = "user@gmail.com";

        User user = createUser(1L);

        Notification notification = createNotification(
                notificationId, 1L, "Welcome", "Hello", "INFO", false);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));

        when(notificationRepository.save(notification))
                .thenReturn(notification);

        NotificationResponseDTO result =
                notificationService.markAsRead(notificationId, email);

        assertNotNull(result);
        assertTrue(notification.getRead());

        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_UserNotFound() {

        String email = "unknown@gmail.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.markAsRead(10L, email));

        verify(notificationRepository, never()).findById(anyLong());
    }

    @Test
    void markAsRead_NotificationNotFound() {

        String email = "user@gmail.com";
        Long notificationId = 100L;

        User user = createUser(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.markAsRead(notificationId, email));

        verify(notificationRepository, never())
                .save(any(Notification.class));
    }

    @Test
    void markAsRead_WhenNotificationBelongsToAnotherUser() {

        String email = "user@gmail.com";

        User user = createUser(1L);

        Notification notification = createNotification(
                10L, 2L, "Private", "Message", "INFO", false);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(10L))
                .thenReturn(Optional.of(notification));

        assertThrows(
                IllegalStateException.class,
                () -> notificationService.markAsRead(10L, email));

        verify(notificationRepository, never())
                .save(any(Notification.class));
    }

    // -------------------------------------------------
    // 7. MARK ALL AS READ
    // -------------------------------------------------

    @Test
    void markAllAsRead_Success() {

        String email = "user@gmail.com";
        User user = createUser(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.markAllAsRead(1L))
                .thenReturn(5);

        int result = notificationService.markAllAsRead(email);

        assertEquals(5, result);

        verify(notificationRepository).markAllAsRead(1L);
    }

    @Test
    void markAllAsRead_WhenNoUnreadNotifications() {

        String email = "user@gmail.com";
        User user = createUser(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.markAllAsRead(1L))
                .thenReturn(0);

        int result = notificationService.markAllAsRead(email);

        assertEquals(0, result);
    }

    @Test
    void markAllAsRead_UserNotFound() {

        String email = "unknown@gmail.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.markAllAsRead(email));

        verify(notificationRepository, never()).markAllAsRead(anyLong());
    }

    // -------------------------------------------------
    // 8. DELETE NOTIFICATION
    // -------------------------------------------------

    @Test
    void deleteNotification_Success() {

        String email = "user@gmail.com";
        Long notificationId = 10L;

        User user = createUser(1L);

        Notification notification = createNotification(
                notificationId, 1L, "Welcome", "Hello", "INFO", false);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.of(notification));

        assertDoesNotThrow(
                () -> notificationService.deleteNotification(
                        notificationId, email));

        verify(notificationRepository).delete(notification);
    }

    @Test
    void deleteNotification_UserNotFound() {

        String email = "unknown@gmail.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.deleteNotification(10L, email));

        verify(notificationRepository, never()).findById(anyLong());

        verify(notificationRepository, never())
                .delete(any(Notification.class));
    }

    @Test
    void deleteNotification_NotificationNotFound() {

        String email = "user@gmail.com";
        Long notificationId = 100L;

        User user = createUser(1L);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(notificationId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> notificationService.deleteNotification(
                        notificationId, email));

        verify(notificationRepository, never())
                .delete(any(Notification.class));
    }

    @Test
    void deleteNotification_WhenNotificationBelongsToAnotherUser() {

        String email = "user@gmail.com";

        User user = createUser(1L);

        Notification notification = createNotification(
                10L, 2L, "Private", "Message", "INFO", false);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(notificationRepository.findById(10L))
                .thenReturn(Optional.of(notification));

        assertThrows(
                IllegalStateException.class,
                () -> notificationService.deleteNotification(10L, email));

        verify(notificationRepository, never())
                .delete(any(Notification.class));
    }
}
