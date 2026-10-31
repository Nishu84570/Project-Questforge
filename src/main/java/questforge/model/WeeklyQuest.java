
package questforge.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(
        name = "weekly_quests",
        uniqueConstraints = {@UniqueConstraint(
        name = "uk_weekly_quest_user_week",
        columnNames = {"user_id", "week_start"})}
)

public class WeeklyQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Week start date is required")
    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @NotBlank(message = "Quest title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    @Column(nullable = false, length = 150)
    private String title;

    @Positive(message = "Target must be greater than zero")
    @Column(nullable = false)
    private long target;

    @PositiveOrZero(message = "Progress cannot be negative")
    @Column(nullable = false)
    private long progress = 0;

    @NotNull(message = "Completed status is required")
    @Column(nullable = false)
    private Boolean completed = false;

    @PositiveOrZero(message = "Reward XP cannot be negative")
    @Column(name = "reward_xp", nullable = false)
    private long rewardXp;

    public WeeklyQuest() {
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

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(LocalDate weekStart) {
        this.weekStart = weekStart;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTarget() {
        return target;
    }

    public void setTarget(long target) {
        this.target = target;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public long getRewardXp() {
        return rewardXp;
    }

    public void setRewardXp(long rewardXp) {
        this.rewardXp = rewardXp;
    }
}
