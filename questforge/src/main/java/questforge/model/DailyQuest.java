package questforge.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "daily_quests",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "quest_date"}
                )
        }
)
public class DailyQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "quest_date", nullable = false)
    private LocalDate questDate;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer target;

    @Column(nullable = false)
    private Integer progress;

    @Column(nullable = false)
    private Boolean completed;

    @Column(nullable = false)
    private Integer rewardXp;

    public DailyQuest() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getQuestDate() {
        return questDate;
    }

    public void setQuestDate(LocalDate questDate) {
        this.questDate = questDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getRewardXp() {
        return rewardXp;
    }

    public void setRewardXp(Integer rewardXp) {
        this.rewardXp = rewardXp;
    }
}