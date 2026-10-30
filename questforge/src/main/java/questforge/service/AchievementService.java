package questforge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.AchievementResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Achievement;
import questforge.model.User;
import questforge.repository.AchievementRepository;
import questforge.repository.UserRepository;

import java.util.List;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    public AchievementService(
            AchievementRepository achievementRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.achievementRepository =
                achievementRepository;

        this.userRepository =
                userRepository;

        this.notificationService =
                notificationService;
    }

    public List<AchievementResponseDTO>
    getUserAchievements(
            String email) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        return achievementRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional
    public AchievementResponseDTO unlockAchievement(
            Integer userId,
            String code,
            String name,
            String description,
            String icon) {

        boolean alreadyUnlocked =
                achievementRepository
                        .existsByUserIdAndCode(
                                userId,
                                code
                        );

        if (alreadyUnlocked) {

            return achievementRepository
                    .findByUserIdAndCode(
                            userId,
                            code
                    )
                    .map(this::convertToResponseDTO)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Achievement not found"
                            )
                    );
        }

        Achievement achievement =
                new Achievement();

        achievement.setUserId(
                userId
        );

        achievement.setCode(
                code
        );

        achievement.setName(
                name
        );

        achievement.setDescription(
                description
        );

        achievement.setIcon(
                icon
        );

        Achievement savedAchievement =
                achievementRepository.save(
                        achievement
                );

        notificationService.createNotification(
                userId,
                "Achievement Unlocked!",
                "You unlocked the achievement: "
                        + name,
                "ACHIEVEMENT"
        );

        return convertToResponseDTO(
                savedAchievement
        );
    }

    private AchievementResponseDTO
    convertToResponseDTO(
            Achievement achievement) {

        return new AchievementResponseDTO(
                achievement.getId(),
                achievement.getCode(),
                achievement.getName(),
                achievement.getDescription(),
                achievement.getIcon()
        );
    }
}