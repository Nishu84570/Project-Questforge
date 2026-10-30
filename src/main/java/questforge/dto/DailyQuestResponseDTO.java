
package questforge.dto;

import java.time.LocalDate;

public class DailyQuestResponseDTO {

    private Long id;
    private LocalDate questDate;
    private String title;
    private long target;
    private long progress;
    private Boolean completed;
    private long rewardXp;

    public DailyQuestResponseDTO() {
    }

    public DailyQuestResponseDTO(
            Long id,
            LocalDate questDate,
            String title,
            long target,
            long progress,
            Boolean completed,
            long rewardXp) {

        this.id = id;
        this.questDate = questDate;
        this.title = title;
        this.target = target;
        this.progress = progress;
        this.completed = completed;
        this.rewardXp = rewardXp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
