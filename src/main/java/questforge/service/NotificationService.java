
package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.NotificationBroadcastRequestDTO;
import questforge.dto.NotificationResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Notification;
import questforge.model.User;
import questforge.repository.NotificationRepository;
import questforge.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public NotificationResponseDTO createNotification(
            Long userId,
            String title,
            String message,
            String type) {

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return convertToResponseDTO(
                notificationRepository.save(notification));
    }

    @Transactional
    public int broadcastNotification(
            NotificationBroadcastRequestDTO request) {

        List<User> users = userRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        List<Notification> notifications = users.stream()
                .map(user -> {
                    Notification notification = new Notification();
                    notification.setUserId(user.getId());
                    notification.setTitle(request.getTitle());
                    notification.setMessage(request.getMessage());
                    notification.setType(request.getType());
                    notification.setRead(false);
                    notification.setCreatedAt(now);
                    return notification;
                })
                .toList();

        notificationRepository.saveAll(notifications);

        return notifications.size();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getMyNotifications(
            String email) {

        User user = getUserByEmail(email);

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getMyUnreadNotifications(
            String email) {

        User user = getUserByEmail(email);

        return notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(
                        user.getId())
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(String email) {

        User user = getUserByEmail(email);

        return notificationRepository
                .countByUserIdAndReadFalse(user.getId());
    }

    @Transactional
    public NotificationResponseDTO markAsRead(
            Long notificationId,
            String email) {

        User user = getUserByEmail(email);
        Notification notification = findNotification(notificationId);

        verifyOwnership(notification, user);

        notification.setRead(true);

        return convertToResponseDTO(
                notificationRepository.save(notification));
    }

    @Transactional
    public int markAllAsRead(String email) {

        User user = getUserByEmail(email);

        return notificationRepository.markAllAsRead(user.getId());
    }

    @Transactional
    public void deleteNotification(
            Long notificationId,
            String email) {

        User user = getUserByEmail(email);
        Notification notification = findNotification(notificationId);

        verifyOwnership(notification, user);

        notificationRepository.delete(notification);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));
    }

    private Notification findNotification(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: "
                                + notificationId));
    }

    private void verifyOwnership(
            Notification notification,
            User user) {

        if (!notification.getUserId().equals(user.getId())) {
            throw new IllegalStateException(
                    "You are not allowed to modify this notification");
        }
    }

    private NotificationResponseDTO convertToResponseDTO(
            Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getRead(),
                notification.getCreatedAt());
    }
}
