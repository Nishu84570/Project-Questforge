
package questforge.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import questforge.dto.ChallengeRequestDTO;
import questforge.dto.ChallengeResponseDTO;
import questforge.model.Challenge;
import questforge.repository.ChallengeRepository;

import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ChallengeService(
            ChallengeRepository challengeRepository) {

        this.challengeRepository = challengeRepository;
    }

    // CREATE
    public ChallengeResponseDTO createChallenge(
            ChallengeRequestDTO request) {

        Challenge challenge = new Challenge();

        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        challenge.setDifficulty(request.getDifficulty());
        challenge.setCategory(request.getCategory());

        Challenge savedChallenge =
                challengeRepository.save(challenge);

        return convertToResponseDTO(savedChallenge);
    }

    // GET ALL
    public List<ChallengeResponseDTO> getAllChallenges() {

        return challengeRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // GET BY ID
    public ChallengeResponseDTO getChallengeById(Integer id) {

        Challenge challenge =
                challengeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Challenge not found"
                                )
                        );

        return convertToResponseDTO(challenge);
    }

    // UPDATE
    public ChallengeResponseDTO updateChallenge(
            Integer id,
            ChallengeRequestDTO request) {

        Challenge existingChallenge =
                challengeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Challenge not found"
                                )
                        );

        existingChallenge.setTitle(request.getTitle());

        existingChallenge.setDescription(
                request.getDescription()
        );

        existingChallenge.setDifficulty(
                request.getDifficulty()
        );

        existingChallenge.setCategory(
                request.getCategory()
        );

        Challenge updatedChallenge =
                challengeRepository.save(existingChallenge);

        return convertToResponseDTO(updatedChallenge);
    }

    // DELETE
    @Transactional
    public void deleteChallenge(Integer id) {

        Challenge existingChallenge =
                challengeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Challenge not found"
                                )
                        );

        /*
         * STEP 1:
         * Delete answers belonging to attempts
         * of this challenge.
         *
         * Answers reference:
         * - attempts through attempt_id
         * - options through option_id
         *
         * Answers must therefore be deleted first.
         */
        entityManager.createNativeQuery(
                """
                DELETE FROM answers
                WHERE attempt_id IN (
                    SELECT id
                    FROM attempts
                    WHERE challenge_id = :challengeId
                )
                """
        )
        .setParameter("challengeId", id)
        .executeUpdate();

        /*
         * STEP 2:
         * Delete attempts belonging to this challenge.
         *
         * Attempts reference challenges through challenge_id.
         */
        entityManager.createNativeQuery(
                """
                DELETE FROM attempts
                WHERE challenge_id = :challengeId
                """
        )
        .setParameter("challengeId", id)
        .executeUpdate();

        /*
         * STEP 3:
         * Delete options belonging to questions
         * of this challenge.
         */
        entityManager.createNativeQuery(
                """
                DELETE FROM options
                WHERE question_id IN (
                    SELECT id
                    FROM questions
                    WHERE challenge_id = :challengeId
                )
                """
        )
        .setParameter("challengeId", id)
        .executeUpdate();

        /*
         * STEP 4:
         * Delete questions belonging to this challenge.
         */
        entityManager.createNativeQuery(
                """
                DELETE FROM questions
                WHERE challenge_id = :challengeId
                """
        )
        .setParameter("challengeId", id)
        .executeUpdate();

        /*
         * STEP 5:
         * Finally delete the challenge.
         */
        challengeRepository.delete(existingChallenge);
    }

    // ENTITY → RESPONSE DTO
    private ChallengeResponseDTO convertToResponseDTO(
            Challenge challenge) {

        return new ChallengeResponseDTO(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getDifficulty(),
                challenge.getCategory()
        );
    }
}

