package questforge.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.DailyQuestResponseDTO;
import questforge.model.DailyQuest;
import questforge.model.User;
import questforge.repository.DailyQuestRepository;
import questforge.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailyQuestService {

    private static final String DAILY_QUEST_TITLE =
            "Complete 1 Challenge";

    private static final int DAILY_QUEST_TARGET = 1;

    private static final int DAILY_QUEST_REWARD_XP = 25;

    private final DailyQuestRepository dailyQuestRepository;

    private final UserRepository userRepository;

    private final XPService xpService;

    private final NotificationService notificationService;

    public DailyQuestService(
            DailyQuestRepository dailyQuestRepository,
            UserRepository userRepository,
            XPService xpService,
            NotificationService notificationService) {

        this.dailyQuestRepository =
                dailyQuestRepository;

        this.userRepository =
                userRepository;

        this.xpService =
                xpService;

        this.notificationService =
                notificationService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void generateDailyQuests() {

        LocalDate today =
                LocalDate.now();

        List<User> users =
                userRepository.findAll();

        for (User user : users) {

            getOrCreateTodayQuest(
                    user.getId(),
                    today
            );
        }
    }

    @Transactional
    public DailyQuest recordChallengeCompletion(
            Integer userId) {

        LocalDate today =
                LocalDate.now();

        DailyQuest quest =
                getOrCreateTodayQuest(
                        userId,
                        today
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
                    "Daily Quest Completed!",
                    "You completed your daily quest and earned "
                            + quest.getRewardXp()
                            + " XP.",
                    "QUEST"
            );
        }

        return dailyQuestRepository.save(
                quest
        );
    }

    public DailyQuest getTodayQuest(
            Integer userId) {

        return getOrCreateTodayQuest(
                userId,
                LocalDate.now()
        );
    }

    public List<DailyQuestResponseDTO>
    getQuestHistory(
            Integer userId) {

        return dailyQuestRepository
                .findByUserIdOrderByQuestDateDesc(
                        userId
                )
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    private DailyQuest getOrCreateTodayQuest(
            Integer userId,
            LocalDate date) {

        return dailyQuestRepository
                .findByUserIdAndQuestDate(
                        userId,
                        date
                )
                .orElseGet(() -> {

                    DailyQuest quest =
                            new DailyQuest();

                    quest.setUserId(
                            userId
                    );

                    quest.setQuestDate(
                            date
                    );

                    quest.setTitle(
                            DAILY_QUEST_TITLE
                    );

                    quest.setTarget(
                            DAILY_QUEST_TARGET
                    );

                    quest.setProgress(
                            0
                    );

                    quest.setCompleted(
                            false
                    );

                    quest.setRewardXp(
                            DAILY_QUEST_REWARD_XP
                    );

                    return dailyQuestRepository.save(
                            quest
                    );
                });
    }

    public DailyQuestResponseDTO
    convertToResponseDTO(
            DailyQuest quest) {

        return new DailyQuestResponseDTO(
                quest.getId(),
                quest.getQuestDate(),
                quest.getTitle(),
                quest.getTarget(),
                quest.getProgress(),
                quest.getCompleted(),
                quest.getRewardXp()
        );
    }
}