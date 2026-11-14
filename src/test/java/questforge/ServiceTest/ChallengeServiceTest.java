
package questforge.ServiceTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import questforge.dto.ChallengeRequestDTO;
import questforge.dto.ChallengeResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Challenge;
import questforge.repository.ChallengeRepository;
import questforge.service.ChallengeService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private ChallengeService challengeService;

    private Challenge challenge;

    @BeforeEach
    void setUp() {

        challenge = new Challenge();
        challenge.setId(1L);
        challenge.setTitle("Java Basics");
        challenge.setDescription("Learn Java fundamentals");
        challenge.setDifficulty(null);
        challenge.setCategory("Programming");

        ReflectionTestUtils.setField(
            challengeService,
            "entityManager",
            entityManager
        );
    }

    @Test
    void createChallenge_Success() {

        ChallengeRequestDTO request = mock(ChallengeRequestDTO.class);

        when(request.getTitle()).thenReturn("Java Basics");
        when(request.getDescription()).thenReturn("Learn Java fundamentals");
        when(request.getDifficulty()).thenReturn(null);
        when(request.getCategory()).thenReturn("Programming");

        when(challengeRepository.save(any(Challenge.class)))
                .thenAnswer(invocation -> {
                    Challenge saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        ChallengeResponseDTO response =
                challengeService.createChallenge(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java Basics", response.getTitle());
        assertEquals("Learn Java fundamentals", response.getDescription());
        assertEquals("Programming", response.getCategory());

        verify(challengeRepository, times(1)).save(any(Challenge.class));
    }


    @Test
    void getAllChallenges_Success() {

        Challenge secondChallenge = new Challenge();
        secondChallenge.setId(2L);
        secondChallenge.setTitle("Spring Boot");
        secondChallenge.setDescription("Learn Spring Boot");
        secondChallenge.setDifficulty(null);
        secondChallenge.setCategory("Backend");

        when(challengeRepository.findAll())
                .thenReturn(List.of(challenge, secondChallenge));

        List<ChallengeResponseDTO> result =
                challengeService.getAllChallenges();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Java Basics", result.get(0).getTitle());
        assertEquals("Spring Boot", result.get(1).getTitle());

        verify(challengeRepository, times(1)).findAll();
    }

 
    @Test
    void getAllChallenges_EmptyList() {

        when(challengeRepository.findAll())
                .thenReturn(List.of());

        List<ChallengeResponseDTO> result =
                challengeService.getAllChallenges();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(challengeRepository, times(1)).findAll();
    }

    @Test
    void getChallengeById_Success() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        ChallengeResponseDTO response =
                challengeService.getChallengeById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java Basics", response.getTitle());
        assertEquals("Learn Java fundamentals", response.getDescription());
        assertEquals("Programming", response.getCategory());

        verify(challengeRepository, times(1)).findById(1L);
    }


    @Test
    void getChallengeById_NotFound() {

        when(challengeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> challengeService.getChallengeById(99L)
        );

        verify(challengeRepository, times(1)).findById(99L);
    }


    @Test
    void updateChallenge_Success() {

        ChallengeRequestDTO request = mock(ChallengeRequestDTO.class);

        when(request.getTitle()).thenReturn("Advanced Java");
        when(request.getDescription()).thenReturn("Advanced Java concepts");
        when(request.getDifficulty()).thenReturn(null);
        when(request.getCategory()).thenReturn("Backend");

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        when(challengeRepository.save(any(Challenge.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChallengeResponseDTO response =
                challengeService.updateChallenge(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Advanced Java", response.getTitle());
        assertEquals("Advanced Java concepts", response.getDescription());
        assertEquals("Backend", response.getCategory());

        verify(challengeRepository, times(1)).findById(1L);
        verify(challengeRepository, times(1)).save(challenge);
    }

    @Test
    void updateChallenge_NotFound() {

        ChallengeRequestDTO request = mock(ChallengeRequestDTO.class);

        when(challengeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> challengeService.updateChallenge(99L, request)
        );

        verify(challengeRepository, times(1)).findById(99L);
        verify(challengeRepository, never()).save(any(Challenge.class));
    }


    @Test
    void deleteChallenge_Success() {

        when(challengeRepository.findById(1L))
                .thenReturn(Optional.of(challenge));

        when(entityManager.createNativeQuery(anyString()))
                .thenReturn(query);

        when(query.setParameter("challengeId", 1L))
                .thenReturn(query);

        when(query.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> challengeService.deleteChallenge(1L));

        verify(challengeRepository, times(1)).findById(1L);

        verify(entityManager, times(5))
                .createNativeQuery(anyString());

        verify(query, times(5))
                .setParameter("challengeId", 1L);

        verify(query, times(5)).executeUpdate();

        verify(challengeRepository, times(1)).delete(challenge);
    }


    @Test
    void deleteChallenge_NotFound() {

        when(challengeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> challengeService.deleteChallenge(99L)
        );

        verify(challengeRepository, times(1)).findById(99L);

        verify(entityManager, never())
                .createNativeQuery(anyString());

        verify(challengeRepository, never())
                .delete(any(Challenge.class));
    }
}
