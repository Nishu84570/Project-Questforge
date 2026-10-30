package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.NotificationResponseDTO;
import questforge.dto.NotificationBroadcastRequestDTO;
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

        this.notificationRepository =
                notificationRepository;

        this.userRepository =
                userRepository;
    }

    // ==========================================
    // CREATE NOTIFICATION
    // ==========================================

    @Transactional
    public NotificationResponseDTO createNotification(
            Integer userId,
            String title,
            String message,
            String type) {

        userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + userId
                        )
                );

        Notification notification =
                new Notification();

        notification.setUserId(
                userId
        );

        notification.setTitle(
                title
        );

        notification.setMessage(
                message
        );

        notification.setType(
                type
        );

        notification.setRead(
                false
        );

        notification.setCreatedAt(
                LocalDateTime.now()
        );

        Notification savedNotification =
                notificationRepository.save(
                        notification
                );

        return convertToResponseDTO(
                savedNotification
        );
    }

    // ==========================================
    // BROADCAST NOTIFICATION
    // ==========================================

    @Transactional
    public int broadcastNotification(
            NotificationBroadcastRequestDTO request) {

        List<User> users =
                userRepository.findAll();

        int notificationCount = 0;

        for (User user : users) {

            Notification notification =
                    new Notification();

            notification.setUserId(
                    user.getId()
            );

            notification.setTitle(
                    request.getTitle()
            );

            notification.setMessage(
                    request.getMessage()
            );

            notification.setType(
                    request.getType()
            );

            notification.setRead(
                    false
            );

            notification.setCreatedAt(
                    LocalDateTime.now()
            );

            notificationRepository.save(
                    notification
            );

            notificationCount++;
        }

        return notificationCount;
    }

    // ==========================================
    // GET ALL NOTIFICATIONS
    // ==========================================

    public List<NotificationResponseDTO>
    getMyNotifications(
            String email) {

        User user =
                getUserByEmail(email);

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // ==========================================
    // GET UNREAD NOTIFICATIONS
    // ==========================================

    public List<NotificationResponseDTO>
    getMyUnreadNotifications(
            String email) {

        User user =
                getUserByEmail(email);

        return notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // ==========================================
    // GET UNREAD NOTIFICATION COUNT
    // ==========================================

    public long getUnreadNotificationCount(
            String email) {

        User user =
                getUserByEmail(email);

        return notificationRepository
                .countByUserIdAndReadFalse(
                        user.getId()
                );
    }

    // ==========================================
    // MARK ONE NOTIFICATION AS READ
    // ==========================================

    @Transactional
    public NotificationResponseDTO markAsRead(
            Integer notificationId,
            String email) {

        User user =
                getUserByEmail(email);

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );

        if (!notification.getUserId()
                .equals(user.getId())) {

            throw new IllegalStateException(
                    "You are not allowed to modify this notification"
            );
        }

        notification.setRead(
                true
        );

        Notification updatedNotification =
                notificationRepository.save(
                        notification
                );

        return convertToResponseDTO(
                updatedNotification
        );
    }

    // ==========================================
    // MARK ALL NOTIFICATIONS AS READ
    // ==========================================

    @Transactional
    public int markAllAsRead(
            String email) {

        User user =
                getUserByEmail(email);

        return notificationRepository
                .markAllAsRead(
                        user.getId()
                );
    }

    // ==========================================
    // DELETE NOTIFICATION
    // ==========================================

    @Transactional
    public void deleteNotification(
            Integer notificationId,
            String email) {

        User user =
                getUserByEmail(email);

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );

        if (!notification.getUserId()
                .equals(user.getId())) {

            throw new IllegalStateException(
                    "You are not allowed to delete this notification"
            );
        }

        notificationRepository.delete(
                notification
        );
    }

    // ==========================================
    // GET USER BY EMAIL
    // ==========================================

    private User getUserByEmail(
            String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    // ==========================================
    // CONVERT TO DTO
    // ==========================================

    private NotificationResponseDTO
    convertToResponseDTO(
            Notification notification) {

        return new NotificationResponseDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getRead(),
                notification.getCreatedAt()
        );
    }
}