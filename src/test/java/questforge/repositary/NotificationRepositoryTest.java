
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.Notification;
import questforge.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification createNotification(
            Long userId,
            String title,
            boolean read,
            LocalDateTime createdAt) {

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage("Test notification message");
        notification.setType("INFO");
        notification.setRead(read);
        notification.setCreatedAt(createdAt);

        return notification;
    }

    @Test
    void shouldSaveAndFindNotificationById() {

        Notification saved =
                notificationRepository.saveAndFlush(
                        createNotification(
                                1L,
                                "Welcome",
                                false,
                                LocalDateTime.of(2026, 11, 13, 10, 0)));

        var result =
                notificationRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1L);
        assertThat(result.get().getTitle()).isEqualTo("Welcome");
        assertThat(result.get().getRead()).isFalse();
        assertThat(result.get().getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindNotificationsByUserIdInDescendingOrder() {

        LocalDateTime time1 =
                LocalDateTime.of(2026, 11, 13, 10, 0);

        LocalDateTime time2 =
                LocalDateTime.of(2026, 11, 13, 11, 0);

        LocalDateTime time3 =
                LocalDateTime.of(2026, 11, 13, 12, 0);

        notificationRepository.save(
                createNotification(1L, "First", false, time1));

        notificationRepository.save(
                createNotification(1L, "Third", true, time3));

        notificationRepository.save(
                createNotification(1L, "Second", false, time2));

        notificationRepository.save(
                createNotification(2L, "Other User", false, time1));

        List<Notification> result =
                notificationRepository.findByUserIdOrderByCreatedAtDesc(1L);

        assertThat(result).hasSize(3);

        assertThat(result)
                .extracting(Notification::getTitle)
                .containsExactly("Third", "Second", "First");
    }

    @Test
    void shouldFindOnlyUnreadNotifications() {

        LocalDateTime time =
                LocalDateTime.of(2026, 11, 13, 10, 0);

        notificationRepository.save(
                createNotification(1L, "Unread One", false, time));

        notificationRepository.save(
                createNotification(1L, "Read One", true, time.plusHours(1)));

        notificationRepository.save(
                createNotification(1L, "Unread Two", false, time.plusHours(2)));

        List<Notification> result =
                notificationRepository
                        .findByUserIdAndReadFalseOrderByCreatedAtDesc(1L);

        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(Notification::getRead)
                .containsOnly(false);

        assertThat(result)
                .extracting(Notification::getTitle)
                .containsExactly("Unread Two", "Unread One");
    }

    @Test
    void shouldCountUnreadNotifications() {

        LocalDateTime time =
                LocalDateTime.of(2026, 11, 13, 10, 0);

        notificationRepository.save(
                createNotification(1L, "First", false, time));

        notificationRepository.save(
                createNotification(1L, "Second", false, time.plusHours(1)));

        notificationRepository.save(
                createNotification(1L, "Third", true, time.plusHours(2)));

        notificationRepository.save(
                createNotification(2L, "Other User", false, time));

        long count =
                notificationRepository.countByUserIdAndReadFalse(1L);

        assertThat(count).isEqualTo(2L);
    }

    @Test
    void shouldReturnZeroWhenNoUnreadNotificationsExist() {

        long count =
                notificationRepository.countByUserIdAndReadFalse(99L);

        assertThat(count).isZero();
    }

    @Test
    void shouldMarkAllUnreadNotificationsAsRead() {

        LocalDateTime time =
                LocalDateTime.of(2026, 11, 13, 10, 0);

        notificationRepository.save(
                createNotification(1L, "First", false, time));

        notificationRepository.save(
                createNotification(1L, "Second", false, time.plusHours(1)));

        notificationRepository.save(
                createNotification(1L, "Already Read", true, time.plusHours(2)));

        notificationRepository.save(
                createNotification(2L, "Other User", false, time));

        notificationRepository.flush();

        int updated =
                notificationRepository.markAllAsRead(1L);

        long unreadCount =
                notificationRepository.countByUserIdAndReadFalse(1L);

        long otherUserUnreadCount =
                notificationRepository.countByUserIdAndReadFalse(2L);

        assertThat(updated).isEqualTo(2);
        assertThat(unreadCount).isZero();
        assertThat(otherUserUnreadCount).isEqualTo(1L);
    }
}
