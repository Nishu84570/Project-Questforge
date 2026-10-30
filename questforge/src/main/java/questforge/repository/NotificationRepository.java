package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import questforge.model.Notification;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Integer> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(
            Integer userId
    );

    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(
            Integer userId
    );

    long countByUserIdAndReadFalse(
            Integer userId
    );

    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.read = true
            WHERE n.userId = :userId
            AND n.read = false
            """)
    int markAllAsRead(
            @Param("userId") Integer userId
    );
}