package questforge.service;

import org.springframework.stereotype.Service;

import questforge.dto.GlobalLeaderboardEntryDTO;
import questforge.dto.LeaderboardEntryDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Challenge;
import questforge.model.Submission;
import questforge.model.User;
import questforge.repository.ChallengeRepository;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private final SubmissionRepository submissionRepository;

    private final UserRepository userRepository;

    private final ChallengeRepository challengeRepository;

    public LeaderboardService(
            SubmissionRepository submissionRepository,
            UserRepository userRepository,
            ChallengeRepository challengeRepository) {

        this.submissionRepository =
                submissionRepository;

        this.userRepository =
                userRepository;

        this.challengeRepository =
                challengeRepository;
    }

    // ==========================================
    // CHALLENGE LEADERBOARD
    // ==========================================

    public List<LeaderboardEntryDTO> getChallengeLeaderboard(
            Integer challengeId) {

        Challenge challenge =
                challengeRepository
                        .findById(challengeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Challenge not found with id: "
                                                + challengeId
                                )
                        );

        List<Submission> submissions =
                submissionRepository
                        .findByChallengeIdAndFirstSubmissionTrue(
                                challengeId
                        );

        // ==========================================
        // SORT BY SCORE
        // ==========================================

        submissions.sort(
                Comparator
                        .comparing(
                                Submission::getScore,
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                Submission::getCorrectAnswers,
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                Submission::getSubmittedAt
                        )
        );

        // ==========================================
        // GET USER IDS
        // ==========================================

        List<Integer> userIds =
                submissions.stream()
                        .map(Submission::getUserId)
                        .distinct()
                        .toList();

        // ==========================================
        // LOAD USERS
        // ==========================================

        Map<Integer, User> users =
                userRepository
                        .findAllById(userIds)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        User::getId,
                                        Function.identity()
                                )
                        );

        // ==========================================
        // BUILD LEADERBOARD
        // ==========================================

        List<LeaderboardEntryDTO> leaderboard =
                new ArrayList<>();

        int rank = 1;

        for (Submission submission : submissions) {

            User user =
                    users.get(
                            submission.getUserId()
                    );

            String userName =
                    user != null
                            ? user.getName()
                            : "Unknown User";

            LeaderboardEntryDTO entry =
                    new LeaderboardEntryDTO(
                            rank,
                            submission.getUserId(),
                            userName,
                            challenge.getId(),
                            challenge.getTitle(),
                            submission.getScore(),
                            submission.getCorrectAnswers(),
                            submission.getTotalQuestions(),
                            submission.getSubmittedAt()
                    );

            leaderboard.add(entry);

            rank++;
        }

        return leaderboard;
    }

    // ==========================================
    // GLOBAL LEADERBOARD
    // ==========================================

    public List<GlobalLeaderboardEntryDTO>
    getGlobalLeaderboard() {

        // ==========================================
        // GET ALL USERS
        // ==========================================

        List<User> users =
                userRepository.findAll();

        // ==========================================
        // SORT BY XP DESCENDING
        // ==========================================

        users.sort(
                Comparator
                        .comparing(
                                this::getUserXp,
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                User::getId
                        )
        );

        // ==========================================
        // BUILD GLOBAL LEADERBOARD
        // ==========================================

        List<GlobalLeaderboardEntryDTO> leaderboard =
                new ArrayList<>();

        int rank = 1;

        for (User user : users) {

            Integer xp =
                    getUserXp(user);

            Integer level =
                    calculateLevel(xp);

            GlobalLeaderboardEntryDTO entry =
                    new GlobalLeaderboardEntryDTO(
                            rank,
                            user.getId(),
                            user.getName(),
                            xp,
                            level
                    );

            leaderboard.add(entry);

            rank++;
        }

        return leaderboard;
    }

    // ==========================================
    // GET USER XP
    // ==========================================

    private Integer getUserXp(User user) {

        if (user.getXp() == null) {
            return 0;
        }

        return user.getXp();
    }

    // ==========================================
    // CALCULATE LEVEL
    // ==========================================

    private Integer calculateLevel(Integer xp) {

        if (xp == null || xp < 100) {
            return 1;
        }

        if (xp < 250) {
            return 2;
        }

        if (xp < 450) {
            return 3;
        }

        if (xp < 700) {
            return 4;
        }

        if (xp < 1000) {
            return 5;
        }

        return 6 + ((xp - 1000) / 500);
    }
}