
package questforge.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.ChallengeRequestDTO;
import questforge.dto.ChallengeResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Challenge;
import questforge.repository.ChallengeRepository;

import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Transactional
    public ChallengeResponseDTO createChallenge(
            ChallengeRequestDTO request) {

        Challenge challenge = new Challenge();
        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        challenge.setDifficulty(request.getDifficulty());
        challenge.setCategory(request.getCategory());

        return convertToResponseDTO(
                challengeRepository.save(challenge));
    }

    @Transactional(readOnly = true)
    public List<ChallengeResponseDTO> getAllChallenges() {
        return challengeRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChallengeResponseDTO getChallengeById(Long id) {
        Challenge challenge = findChallenge(id);
        return convertToResponseDTO(challenge);
    }

    @Transactional
    public ChallengeResponseDTO updateChallenge(
            Long id,
            ChallengeRequestDTO request) {

        Challenge challenge = findChallenge(id);

        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        challenge.setDifficulty(request.getDifficulty());
        challenge.setCategory(request.getCategory());

        return convertToResponseDTO(
                challengeRepository.save(challenge));
    }

    @Transactional
    public void deleteChallenge(Long id) {

        Challenge challenge = findChallenge(id);

        entityManager.createNativeQuery("""
                DELETE FROM answers
                WHERE attempt_id IN (
                    SELECT id FROM attempts
                    WHERE challenge_id = :challengeId
                )
                """)
                .setParameter("challengeId", id)
                .executeUpdate();


        entityManager.createNativeQuery("""
                DELETE FROM attempts
                WHERE challenge_id = :challengeId
                """)
                .setParameter("challengeId", id)
                .executeUpdate();

 
        entityManager.createNativeQuery("""
                DELETE FROM submissions
                WHERE challenge_id = :challengeId
                """)
                .setParameter("challengeId", id)
                .executeUpdate();

    
        entityManager.createNativeQuery("""
                DELETE FROM options
                WHERE question_id IN (
                    SELECT id FROM questions
                    WHERE challenge_id = :challengeId
                )
                """)
                .setParameter("challengeId", id)
                .executeUpdate();

        entityManager.createNativeQuery("""
                DELETE FROM questions
                WHERE challenge_id = :challengeId
                """)
                .setParameter("challengeId", id)
                .executeUpdate();

     
        challengeRepository.delete(challenge);
    }

    private Challenge findChallenge(Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge not found with id: " + id));
    }

    private ChallengeResponseDTO convertToResponseDTO(
            Challenge challenge) {
        return new ChallengeResponseDTO(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getDifficulty(),
                challenge.getCategory());
    }
}
