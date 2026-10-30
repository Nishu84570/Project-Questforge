package questforge.dto;

import java.time.LocalDate;

public class WeeklyQuestResponseDTO {

    private Integer id;

    private LocalDate weekStart;

    private String title;

    private Integer target;

    private Integer progress;

    private Boolean completed;

    private Integer rewardXp;

    public WeeklyQuestResponseDTO() {
    }

    public WeeklyQuestResponseDTO(
            Integer id,
            LocalDate weekStart,
            String title,
            Integer target,
            Integer progress,
            Boolean completed,
            Integer rewardXp) {

        this.id = id;
        this.weekStart = weekStart;
        this.title = title;
        this.target = target;
        this.progress = progress;
        this.completed = completed;
        this.rewardXp = rewardXp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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