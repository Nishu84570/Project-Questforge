package questforge.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.WeeklyQuestResponseDTO;
import questforge.model.User;
import questforge.model.WeeklyQuest;
import questforge.repository.UserRepository;
import questforge.repository.WeeklyQuestRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class WeeklyQuestService {

    private static final String WEEKLY_QUEST_TITLE =
            "Complete 5 Challenges";

    private static final int WEEKLY_QUEST_TARGET = 5;

    private static final int WEEKLY_QUEST_REWARD_XP = 150;

    private final WeeklyQuestRepository weeklyQuestRepository;

    private final UserRepository userRepository;

    private final XPService xpService;

    private final NotificationService notificationService;

    public WeeklyQuestService(
            WeeklyQuestRepository weeklyQuestRepository,
            UserRepository userRepository,
            XPService xpService,
            NotificationService notificationService) {

        this.weeklyQuestRepository =
                weeklyQuestRepository;

        this.userRepository =
                userRepository;

        this.xpService =
                xpService;

        this.notificationService =
                notificationService;
    }

    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void generateWeeklyQuests() {

        LocalDate today =
                LocalDate.now();

        LocalDate weekStart =
                getWeekStart(today);

        List<User> users =
                userRepository.findAll();

        for (User user : users) {

            getOrCreateCurrentWeekQuest(
                    user.getId(),
                    weekStart
            );
        }
    }

    @Transactional
    public WeeklyQuest recordChallengeCompletion(
            Integer userId) {

        LocalDate weekStart =
                getWeekStart(
                        LocalDate.now()
                );

        WeeklyQuest quest =
                getOrCreateCurrentWeekQuest(
                        userId,
                        weekStart
                );

        if (Boolean.TRUE.equals(
                quest.getCompleted())) {

            return quest;
        }

        int progress =
                quest.getProgress() == null
                        ? 0
                        : quest.getProgress();

        progress++;

        quest.setProgress(
                Math.min(
                        progress,
                        quest.getTarget()
                )
        );

        if (quest.getProgress()
                >= quest.getTarget()) {

            quest.setCompleted(true);

            xpService.awardDailyQuestXp(
                    userId,
                    quest.getRewardXp()
            );

            notificationService.createNotification(
                    userId,
                    "Weekly Quest Completed!",
                    "You completed your weekly quest and earned "
                            + quest.getRewardXp()
                            + " XP.",
                    "QUEST"
            );
        }

        return weeklyQuestRepository.save(
                quest
        );
    }

    public WeeklyQuest getCurrentWeekQuest(
            Integer userId) {

        LocalDate weekStart =
                getWeekStart(
                        LocalDate.now()
                );

        return getOrCreateCurrentWeekQuest(
                userId,
                weekStart
        );
    }

    public List<WeeklyQuestResponseDTO>
    getQuestHistory(
            Integer userId) {

        return weeklyQuestRepository
                .findByUserIdOrderByWeekStartDesc(
                        userId
                )
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    private WeeklyQuest getOrCreateCurrentWeekQuest(
            Integer userId,
            LocalDate weekStart) {

        return weeklyQuestRepository
                .findByUserIdAndWeekStart(
                        userId,
                        weekStart
                )
                .orElseGet(() -> {

                    WeeklyQuest quest =
                            new WeeklyQuest();

                    quest.setUserId(
                            userId
                    );

                    quest.setWeekStart(
                            weekStart
                    );

                    quest.setTitle(
                            WEEKLY_QUEST_TITLE
                    );

                    quest.setTarget(
                            WEEKLY_QUEST_TARGET
                    );

                    quest.setProgress(
                            0
                    );

                    quest.setCompleted(
                            false
                    );

                    quest.setRewardXp(
                            WEEKLY_QUEST_REWARD_XP
                    );

                    return weeklyQuestRepository.save(
                            quest
                    );
                });
    }

    private LocalDate getWeekStart(
            LocalDate date) {

        return date.with(
                DayOfWeek.MONDAY
        );
    }

    public WeeklyQuestResponseDTO
    convertToResponseDTO(
            WeeklyQuest quest) {

        return new WeeklyQuestResponseDTO(
                quest.getId(),
                quest.getWeekStart(),
                quest.getTitle(),
                quest.getTarget(),
                quest.getProgress(),
                quest.getCompleted(),
                quest.getRewardXp()
        );
    }
}