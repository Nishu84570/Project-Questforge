
package questforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

@Entity
@Table(
    name = "user_streaks",
    uniqueConstraints = {@UniqueConstraint(
    name = "uk_user_streaks_user_id",
    columnNames = {"user_id"})}
)

public class UserStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @PositiveOrZero(message = "Current streak cannot be negative")
    @Column(name = "current_streak", nullable = false)
    private long currentStreak = 0L;

    @PositiveOrZero(message = "Longest streak cannot be negative")
    @Column(name = "longest_streak", nullable = false)
    private long longestStreak = 0L;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    public UserStreak() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public long getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(long currentStreak) {
        this.currentStreak = currentStreak;
    }

    public long getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(long longestStreak) {
        this.longestStreak = longestStreak;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }
}
